/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.core.YamlSequence;
import com.opentext.ia.yaml.resource.ResourceResolver;


public class IncludeExternalYaml implements Visitor {

  private static final String INCLUDES = "includes";
  private static final String CONFIGURE = "configure";

  private final ResourceResolver resourceResolver;

  public IncludeExternalYaml(ResourceResolver resourceResolver) {
    this.resourceResolver = resourceResolver;
  }

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    yaml.get(INCLUDES).toList().forEach(value -> include(value.toString(), yaml));
    yaml.remove(INCLUDES);
  }

  private void include(String resource, YamlMap target) {
    YamlMap include = YamlMap.from(resourceResolver.apply(resource));
    include.visit(new ResolveResources(resource));
    include.entries().forEach(entry -> includeEntry(entry.getKey(), entry.getValue(), target));
  }

  private void includeEntry(String key, Value value, YamlMap target) {
    switch (getDuplication(key, value, target)) {
      case DUPLICATION:
        throw new IllegalArgumentException(String.format(
            "Duplicate key %s: cannot set to %s because it's already set to %s", key, value, target.get(key)));
      case NO_DUPLICATION:
        target.put(key, value);
        break;
      case IGNORE_DUPLICATION:
        break;
      default:
        throw new UnsupportedOperationException("Unhandled " + Duplication.class.getName());
    }
  }

  private Duplication getDuplication(String key, Value value, YamlMap target) {
    if (!target.containsKey(key)) {
      return Duplication.NO_DUPLICATION;
    }
    YamlMap map = toMap(value);
    if (map == null) {
      return Duplication.DUPLICATION;
    }
    if (ObjectConfiguration.USE_EXISTING.equals(ObjectConfiguration.parse(map.get(CONFIGURE).toString()))) {
      return Duplication.IGNORE_DUPLICATION;
    }
    return Duplication.DUPLICATION;
  }

  private YamlMap toMap(Value value) {
    if (value.isMap()) {
      return value.toMap();
    }
    if (value.isList()) {
      YamlSequence values = value.toList();
      if (values.size() != 1) {
        return null;
      }
      Value item = values.get(0);
      if (item.isMap()) {
        return item.toMap();
      }
    }
    return null;
  }


  private enum Duplication {

    NO_DUPLICATION, IGNORE_DUPLICATION, DUPLICATION;

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
