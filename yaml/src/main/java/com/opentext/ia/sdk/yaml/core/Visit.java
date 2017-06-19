/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.core;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Visit {

  private static final String SEPARATOR = "/";

  private final YamlMap rootMap;
  private final YamlMap map;
  private final String path;
  private final int level;

  Visit(YamlMap map) {
    this(map, map, "", 0);
  }

  Visit(YamlMap rootMap, YamlMap map, String path, int level) {
    this.rootMap = rootMap;
    this.map = map;
    this.path = path;
    this.level = level;
  }

  public YamlMap getRootMap() {
    return rootMap;
  }

  public YamlMap getMap() {
    return map;
  }

  public String getPath() {
    return path.isEmpty() ? SEPARATOR : path;
  }

  public int getLevel() {
    return level;
  }

  Visit descend(Object... keys) {
    return new Visit(rootMap, map.get(keys).toMap(), appendPath(keys), level + keys.length);
  }

  private String appendPath(Object... keys) {
    return path + SEPARATOR + Arrays.asList(keys).stream()
        .map(Object::toString)
        .collect(Collectors.joining(SEPARATOR));
  }

  @Override
  public String toString() {
    return getPath() + " -> " + getMap();
  }

}
