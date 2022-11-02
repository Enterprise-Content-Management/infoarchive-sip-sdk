/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.ListIterator;
import java.util.function.Function;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;


public class StringSubstitutor implements Visitor {

  private final Function<String, String> substitutor;

  public StringSubstitutor(Function<String, String> substitutor) {
    this.substitutor = substitutor;
  }

  @Override
  public int maxNesting() {
    return substitutor == null ? 0 : Visitor.super.maxNesting();
  }

  @Override
  public boolean test(Visit visit) {
    return substitutor != null;
  }

  @Override
  public void accept(Visit visit) {
    boolean isResourceContainer = YamlConstants.RESOURCE_CONTAINER_PATHS.stream()
        .anyMatch(visit.getPath()::matches);
    YamlMap yaml = visit.getMap();
    yaml.entries()
        .filter(entry -> entry.getValue().isString())
        .filter(entry -> !isResourceContainer || !YamlConstants.TEXT.equals(entry.getKey()))
        .forEach(this::substituteValue);
    yaml.entries()
        .filter(this::isListOfStrings)
        .forEach(this::substituteValues);
  }

  private void substituteValue(Entry entry) {
    entry.getParent().replace(entry.getKey(), substituteValue(entry.getValue()));
  }

  private String substituteValue(Value value) {
    return substitutor.apply(value.toString());
  }

  private boolean isListOfStrings(Entry entry) {
    Value value = entry.getValue();
    return value.isList() && value.toList().stream().allMatch(Value::isString);
  }

  private void substituteValues(Entry entry) {
    ListIterator<Value> iterator = entry.getValue().toList().listIterator();
    while (iterator.hasNext()) {
      String substitutedValue = substituteValue(iterator.next());
      iterator.set(new Value(substitutedValue));
    }
  }

}
