/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.yaml;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class Value {

  private final Object data;

  public Value(Object data) {
    this.data = data;
  }

  public boolean isEmpty() {
    return data == null;
  }

  public boolean isMap() {
    return data instanceof Map;
  }

  public YamlMap toMap() {
    if (!isMap()) {
      return new YamlMap();
    }
    return new YamlMap(data);
  }

  public boolean isList() {
    return data instanceof List;
  }

  @SuppressWarnings("unchecked")
  public List<Value> toList() {
    if (!isList()) {
      return Collections.emptyList();
    }
    return ((List<?>)data).stream()
        .map(Value::new)
        .collect(Collectors.toList());
  }

  public boolean toBoolean() {
    return Boolean.parseBoolean(toString());
  }

  @Override
  public String toString() {
    return data == null ? "" : data.toString();
  }

  public int toInt() {
    try {
      return Integer.parseInt(toString());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public double toDouble() {
    try {
      return Double.parseDouble(toString());
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  public Object getRawData() {
    return data;
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Value) {
      Value other = (Value)obj;
      return Objects.equals(data, other.data);
    }
    return Objects.equals(data, obj);
  }

  public boolean isScalar() {
    return !isEmpty() && !isList() && !isMap();
  }

}
