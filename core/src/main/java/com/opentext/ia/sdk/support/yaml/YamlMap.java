/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


/**
 * Type-safe access to a YAML structure.
 */
public class YamlMap {

  private final Map<String, Object> data;

  public YamlMap() {
    this(null);
  }

  @SuppressWarnings("unchecked")
  public YamlMap(Object data) {
    this.data = data instanceof Map ? (Map<String, Object>)data : new HashMap<>();
  }

  public boolean isEmpty() {
    return data.isEmpty();
  }

  public int size() {
    return data.size();
  }

  public YamlMap put(String key, Object value) {
    data.put(key, unpack(value));
    return this;
  }

  private Object unpack(Object value) {
    if (value instanceof YamlMap) {
      return ((YamlMap)value).getRawData();
    }
    if (value instanceof Value) {
      return ((Value)value).getRawData();
    }
    return value;
  }

  public Map<String, Object> getRawData() {
    return data;
  }

  public void putAll(YamlMap other) {
    data.putAll(other.data);
  }

  public boolean containsKey(String key) {
    return data.containsKey(key);
  }

  public YamlMap remove(String key) {
    data.remove(key);
    return this;
  }

  public Value get(Object... keys) {
    YamlMap map = this;
    int i = 0;
    while (i < keys.length - 1) {
      Value value = map.get(keys[i]);
      if (value.isList()) {
        map = value.toList().get((int)keys[++i]).toMap();
      }
      i++;
    }
    return new Value(map.data.get(keys[keys.length - 1]));
  }

  public Stream<Value> values() {
    return entries().map(Entry::getValue);
  }

  public Stream<Entry> entries() {
    return data.entrySet().stream()
        .map(entry -> new Entry(entry.getKey(), new Value(entry.getValue())))
        .sorted();
  }

  @Override
  public String toString() {
    return data.toString();
  }

}
