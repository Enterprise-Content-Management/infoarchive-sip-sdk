/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.opentext.ia.sdk.yaml.core.Entry;
import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.YamlMap;


class MapOfMapsToSequence implements Predicate<Entry>, Consumer<Entry> {

  private final YamlMap yaml;

  MapOfMapsToSequence(YamlMap yaml) {
    this.yaml = yaml;
  }

  @Override
  public boolean test(Entry entry) {
    if (!entry.getValue().isMap()) {
      return false;
    }
    YamlMap map = entry.getValue().toMap();
    long numChildMapsWithoutName = map.values()
        .filter(Value::isMap)
        .map(Value::toMap)
        .filter(nestedMap -> !nestedMap.containsKey("name"))
        .count();
    return numChildMapsWithoutName > 0 && numChildMapsWithoutName == map.values().count();
  }

  @Override
  public void accept(Entry entry) {
    yaml.put(entry.getKey(), toSequence(entry.getValue().toMap()));
  }

  private static List<?> toSequence(YamlMap map) {
    return map.entries()
        .map(Entry::toMap)
        .collect(Collectors.toList());
  }

}
