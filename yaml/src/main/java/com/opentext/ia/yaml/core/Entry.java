/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.util.Objects;


public class Entry implements Comparable<Entry> {

  private final String key;
  private final Value value;
  private final YamlMap parent;

  public Entry(YamlMap parent, String key, Value value) {
    this.parent = parent;
    this.key = key;
    this.value = value;
  }

  public YamlMap getParent() {
    return parent;
  }

  public String getKey() {
    return key;
  }

  public Value getValue() {
    return value;
  }

  public YamlMap toMap() {
    YamlMap result = new YamlMap();
    result.putAll(getValue().toMap());
    result.put("name", getKey());
    return result;
  }

  @Override
  public String toString() {
    return String.format("%s = %s", key, value);
  }

  @Override
  public int compareTo(Entry other) {
    return key.compareTo(other.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Entry) {
      return compareTo((Entry)obj) == 0;
    }
    return false;
  }

}
