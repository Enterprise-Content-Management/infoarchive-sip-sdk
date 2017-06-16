/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.yaml;

import java.util.*;
import java.util.stream.IntStream;
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object unpack(Object value) {
    if (value instanceof YamlMap) {
      return ((YamlMap)value).getRawData();
    }
    if (value instanceof Iterable) {
      Collection<Object> result = new ArrayList<>();
      ((Iterable)value).forEach(v -> result.add(unpack(v)));
      return result;
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
      Value value = map.get(keys[i++]);
      if (value.isList()) {
        int index = (int)keys[i++];
        value = value.toList().get(index);
        if (i < keys.length) {
          map = value.toMap();
        } else {
          return value;
        }
      } else {
        map = value.toMap();
      }
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

  public void visit(Visitor visitor) {
    visit(visitor, new Visit(this));
  }

  private void visit(Visitor visitor, Visit visit) {
    if (visitor.test(visit)) {
      visitor.accept(visit);
    }
    if (visitor.maxNesting() > visit.getLevel()) {
      visit.getMap().entries().forEach(entry -> {
        String key = entry.getKey();
        Value value = entry.getValue();
        if (value.isMap()) {
          visit(visitor, visit.descend(key));
        } else if (isListOfMaps(value)) {
          IntStream.range(0, value.toList().size())
              .forEach(index -> visit(visitor, visit.descend(key, index)));
        }
      });
    }
  }

  private boolean isListOfMaps(Value value) {
    List<Value> values = value.toList();
    if (values.isEmpty()) {
      return false;
    }
    return values.size() == values.stream()
        .filter(Value::isMap)
        .count();
  }

  @Override
  public String toString() {
    return data.toString();
  }


}
