/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import java.io.Serializable;
import java.util.Comparator;


class DefaultYamlComparator implements Comparator<String>, Serializable {

  private static final String VERSION = "version";
  private static final String NAME = "name";

  @Override
  public int compare(String key1, String key2) {
    int result = compare(VERSION, key1, key2);
    if (result != 0) {
      return result;
    }
    result = compare(NAME, key1, key2);
    if (result != 0) {
      return result;
    }
    return key1.compareTo(key2);
  }

  private int compare(String property, String key1, String key2) {
    if (property.equals(key1)) {
      return property.equals(key2) ? 0 : -1;
    }
    if (property.equals(key2)) {
      return 1;
    }
    return 0;
  }

}
