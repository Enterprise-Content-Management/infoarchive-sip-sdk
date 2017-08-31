/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class DefaultYamlSequenceComparator implements Comparator<Object>, Serializable {

  private static final String NAME = "name";

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public int compare(Object o1, Object o2) {
    Map<String, Object> m1 = (Map<String, Object>)o1;
    Map<String, Object> m2 = (Map<String, Object>)o2;
    int result = m1.get(NAME).toString().compareTo(m2.get(NAME).toString());
    if (result != 0) {
      return result;
    }
    Set<String> keys = new TreeSet<>(m1.keySet());
    keys.addAll(m2.keySet());
    keys.remove(NAME);
    for (String key : keys) {
      Object value1 = m1.get(key);
      Object value2 = m2.get(key);
      result = compareNulls(value1, value2);
      if (result != 0) {
        return result;
      }
      result = compareValues(value1, value2);
      if (result != 0) {
        return result;
      }
    }
    return 0;
  }

  private int compareNulls(Object value1, Object value2) {
    if (value1 == null) {
      return value2 == null ? 0 : -1;
    }
    if (value2 == null) {
      return 1;
    }
    return 0;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private int compareValues(Object value1, Object value2) {
    if (!value1.getClass().equals(value2.getClass())) {
      return 0;
    }
    if (value2 instanceof Comparable) {
      return ((Comparable)value1).compareTo(value2);
    }
    return 0;
  }

}
