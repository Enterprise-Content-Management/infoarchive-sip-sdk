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

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public int compare(Object o1, Object o2) {
    if (o1 instanceof Map && o2 instanceof Map) {
      return compareMaps((Map<String, Object>)o1, (Map<String, Object>)o2);
    }
    int result = compareNulls(o1, o2);
    if (result != 0 || o1 == null) {
      return result;
    }
    return compareValues(o1, o2);
  }

  private int compareMaps(Map<String, Object> m1, Map<String, Object> m2) {
    int result = 0;
    Set<String> keys = new TreeSet<>(new DefaultYamlComparator());
    keys.addAll(m1.keySet());
    keys.addAll(m2.keySet());
    for (String key : keys) {
      Object value1 = m1.get(key);
      Object value2 = m2.get(key);
      result = compareNulls(value1, value2);
      if (result != 0) {
        return result;
      }
      if (value1 == null) {
        continue;
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
    if (value1 instanceof Comparable) {
      return ((Comparable)value1).compareTo(value2);
    }
    return 0;
  }

}
