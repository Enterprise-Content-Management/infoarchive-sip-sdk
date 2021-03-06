/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.net.URI;
import java.util.Collections;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.core.YamlSequence;
import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


public class IncludeExternalYaml implements Visitor {

  private static final String VERSION = "version";
  private static final String INCLUDES = "includes";
  private static final String CONFIGURE = "configure";
  private static final String RESOURCE = "resource";

  private final ResourceResolver resourceResolver;
  private final ConfigurationProperties parent;

  public IncludeExternalYaml(ResourceResolver resourceResolver, ConfigurationProperties parent) {
    this.resourceResolver = resourceResolver;
    this.parent = parent;
  }

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    YamlSequence includeFiles = yaml.get(INCLUDES).toList();
    yaml.remove(INCLUDES);
    includeFiles.forEach(value -> include(value, yaml));
  }

  private void include(Value include, YamlMap target) {
    String resource;
    if (include.isMap()) {
      YamlMap map = include.toMap();
      if (ObjectConfiguration.parse(map.get(CONFIGURE).toString()).shouldIgnoreObject()) {
        return;
      }
      resource = map.get(RESOURCE).toString();
    } else {
      resource = include.toString();
    }
    include(resource, target);
  }

  private void include(String resource, YamlMap target) {
    URI base = URI.create(resource);
    ConfigurationProperties properties = propertiesIn(base);
    YamlMap include = YamlMap.from(resourceResolver.apply(resource));
    include.visit(includeNestedYaml(base, properties));
    include.visit(new ResolveResources(resource));
    include.visit(new StringSubstitutor(properties));
    include.entries().forEach(entry -> includeEntry(entry.getKey(), entry.getValue(), target));
  }

  private ConfigurationProperties propertiesIn(URI resource) {
    String propertiesResource = resource.resolve("configuration.properties").toString();
    try {
      return new ConfigurationProperties(resourceResolver, propertiesResource, parent);
    } catch (UnknownResourceException e) {
      return parent;
    }
  }

  private IncludeExternalYaml includeNestedYaml(URI base, ConfigurationProperties properties) {
    return new IncludeExternalYaml(name -> resourceResolver.apply(base.resolve(name).toString()), properties);
  }

  private void includeEntry(String key, Value value, YamlMap target) {
    if (VERSION.equals(key)) {
      assertSameVersion(value, target);
      return;
    }
    if ("namespace".equals(key) || "namespaces".equals(key)) {
      return;
    }
    String type = value.isList() ? findSingular(target, key) : key;
    String collection = type.isEmpty() ? key : English.plural(type);
    includeEntry(key, type, collection, value, target);
  }

  private void assertSameVersion(Value version, YamlMap target) {
    if (!version.toString().equals(target.get(VERSION).toString())) {
      throw new IllegalArgumentException(String.format("Different versions of configuration format: %s vs %s",
          version, target.get(VERSION)));
    }
  }

  private String findSingular(YamlMap target, String plural) {
    return target.entries()
        .map(Entry::getKey)
        .filter(key -> plural.equals(English.plural(key)))
        .findAny()
        .orElse("");
  }

  private void includeEntry(String key, String type, String collection, Value value, YamlMap target) {
    Value targetValue = target.containsKey(type) ? target.get(type) : target.get(collection);
    if (targetValue.isEmpty()) {
      target.put(key, value);
      return;
    }
    if (!canConfigure(value)) {
      return;
    }
    if (!canConfigure(targetValue)) {
      target.remove(type)
          .remove(collection)
          .put(key, value);
      return;
    }
    if (target.containsKey(type)) {
      target.replace(type, collection, Collections.singletonList(targetValue));
    }
    YamlSequence values = target.get(collection).toList();
    if (value.isList()) {
      values.addAll(value.toList());
    } else {
      values.add(value);
    }
  }

  private boolean canConfigure(Value value) {
    YamlMap map = value.isList() ? value.toList().get(0).toMap() : value.toMap();
    return ObjectConfiguration.parse(map.get(CONFIGURE).toString()).mayCreateObject();
  }


  private static class ResolveResources implements Visitor {

    private static final String RESOURCE = "resource";

    private final String prefix;

    ResolveResources(String fileName) {
      int index = fileName.lastIndexOf('/');
      this.prefix = index < 0 ? "" : fileName.substring(0, index + 1);
    }

    @Override
    public int maxNesting() {
      return prefix.isEmpty() ? 1 : Visitor.super.maxNesting();
    }

    @Override
    public boolean test(Visit visit) {
      return !prefix.isEmpty();
    }

    @Override
    public void accept(Visit visit) {
      YamlMap yaml = visit.getMap();
      if (yaml.containsKey(RESOURCE)) {
        yaml.replace(RESOURCE, prefix + yaml.get(RESOURCE));
      }
    }

  }

}
