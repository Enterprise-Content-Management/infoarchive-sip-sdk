/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;

class MapOfMapsToSequence implements Predicate<Entry>, Consumer<Entry> {

  private final YamlMap yaml;

  MapOfMapsToSequence(YamlMap yaml) {
    this.yaml = yaml;
  }

  @Override
  public boolean test(Entry entry) {
    Value value = entry.getValue();
    if (value.isList()) {
      return value.toList().stream().map(this::isMapOfMaps).reduce(true, (a, b) -> a && b)
          .booleanValue();
    }
    return isMapOfMaps(value);
  }

  private Boolean isMapOfMaps(Value value) {
    YamlMap map = value.toMap();
    long numChildMapsWithoutName = map.values().filter(Value::isMap).map(Value::toMap)
        .filter(nestedMap -> !nestedMap.containsKey("name")).count();
    return Boolean
        .valueOf(numChildMapsWithoutName > 0 && numChildMapsWithoutName == map.values().count());
  }

  @Override
  public void accept(Entry entry) {
    yaml.put(entry.getKey(), toSequence(entry));
  }

  private static List<?> toSequence(Entry entry) {
    Value value = entry.getValue();
    if (value.isList()) {
      return value.toList().stream().map(Value::toMap).flatMap(YamlMap::entries).map(Entry::toMap)
          .collect(Collectors.toList());
    }
    return value.toMap().entries().map(Entry::toMap).collect(Collectors.toList());
  }

}
