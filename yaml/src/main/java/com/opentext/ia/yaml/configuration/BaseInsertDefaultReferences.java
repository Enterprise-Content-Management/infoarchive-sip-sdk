/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.PropertyVisitor;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


public abstract class BaseInsertDefaultReferences extends PropertyVisitor {

  private static final String NAME = "name";
  private static final String NAMESPACE = "namespace";
  private static final String DEFAULT = "default";

  public BaseInsertDefaultReferences(Map<String, Collection<String>> propertiesByPathRegex) {
    super(propertiesByPathRegex);
  }

  @Override
  protected void visitProperty(Visit visit, String property) {
    YamlMap yaml = visit.getMap();
    if (missesProperty(visit, property)) {
      getDefaultValueFor(visit, typeOf(property))
          .ifPresent(defaultValue -> yaml.put(property, defaultValue));
    }
  }

  private Optional<String> getDefaultValueFor(Visit visit, String type) {
    return getDefaultInstance(visit, English.plural(type), idPropertyFor(type));
  }

  private String idPropertyFor(String type) {
    return NAMESPACE.equals(type) ? "prefix" : NAME;
  }

  private Optional<String> getDefaultInstance(Visit visit, String collection, String idProperty) {
    List<Value> instances = visit.getRootMap().get(collection).toList();
    if (instances.size() == 1) {
      return Optional.of(instances.get(0).toMap().get(idProperty).toString());
    }
    return instances.stream()
        .map(Value::toMap)
        .filter(map -> map.get(DEFAULT).toBoolean())
        .map(map -> map.get(idProperty).toString())
        .findAny();
  }

  protected abstract boolean missesProperty(Visit visit, String property);

  protected abstract String typeOf(String property);

}
