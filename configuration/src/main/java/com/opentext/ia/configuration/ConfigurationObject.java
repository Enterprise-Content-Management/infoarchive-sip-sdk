/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;


public class ConfigurationObject {

  private final JSONObject properties = new JSONObject();
  private final Map<String, List<ConfigurationObject>> childObjects = new HashMap<>();
  private final String type;

  public ConfigurationObject(String type) {
    this.type = type;
  }

  public JSONObject getProperties() {
    return properties;
  }

  public Map<String, List<ConfigurationObject>> getChildObjects() {
    return childObjects;
  }

  public String getType() {
    return type;
  }

  public void setProperty(String name, Object value) {
    properties.put(name, value);
  }

  public void addChildObject(String collection, ConfigurationObject childObject) {
    childObjects.computeIfAbsent(collection, ignored -> new ArrayList<>()).add(childObject);
  }

  @Override
  public String toString() {
    return toString("");
  }

  private String toString(String indent) {
    StringBuilder result = new StringBuilder();
    result.append(indent).append("properties:").append(System.lineSeparator()).append(indent).append("  ")
        .append(properties.toString(4 + indent.length())).append(System.lineSeparator());
    childObjects.forEach((collection, objects) -> {
      result.append(indent).append(collection).append(':').append(System.lineSeparator());
      if (objects.isEmpty()) {
        result.append(indent).append("  []").append(System.lineSeparator());
      } else {
        objects.forEach(object -> {
          result.append(indent).append(object.toString(indent + "  ")).append(System.lineSeparator());
        });
      }
    });
    return result.toString();
  }

}
