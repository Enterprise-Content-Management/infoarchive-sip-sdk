/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;


/**
 * An object being configured.
 * @author Ray Sinnema
 * @since 9.4.0
 */
public class ConfigurationObject {

  private static final String NL = System.lineSeparator();
  private final JSONObject properties = new JSONObject();
  private final Map<String, List<ConfigurationObject>> childObjects = new HashMap<>();
  private final String type;

  public ConfigurationObject(String type) {
    this.type = type;
  }

  /**
   * Returns the object's properties as JSON.
   * @return The object's properties as JSON
   */
  public JSONObject getProperties() {
    return properties;
  }

  /**
   * Returns the objects owned by this object.
   * @return The objects owned by this object
   */
  public Map<String, List<ConfigurationObject>> getChildObjects() {
    return childObjects;
  }

  /**
   * Returns the object's type.
   * @return The object's type
   */
  public String getType() {
    return type;
  }

  /**
   * Set the value of a property.
   * @param name The name of the property to set
   * @param value The value to set the property to
   */
  public void setProperty(String name, Object value) {
    properties.put(name, toJsonValue(value));
  }

  private Object toJsonValue(Object value) {
    if (value == null) {
      return JSONObject.NULL;
    }
    return value;
  }

  public boolean hasProperty(String name) {
    return properties.has(name) && properties.get(name) != JSONObject.NULL;
  }

  /**
   * Add an object that is to be owned by this object.
   * @param collection The collection to store the child object under
   * @param childObject The child object to add
   */
  public void addChildObject(String collection, ConfigurationObject childObject) {
    childObjects.computeIfAbsent(collection, ignored -> new ArrayList<>()).add(childObject);
  }

  @Override
  public String toString() {
    return toString("");
  }

  private String toString(String indent) {
    StringBuilder result = new StringBuilder();
    result.append(indent).append("properties:").append(NL).append(indent).append("  ")
        .append(properties.toString(4 + indent.length())).append(NL);

    childObjects.forEach((collection, objects) -> {
      result.append(indent).append(collection).append(':').append(NL);
      if (objects.isEmpty()) {
        result.append(indent).append("  []").append(NL);
      } else {
        objects.forEach(object ->
            result.append(indent).append(object.toString(indent + "  ")).append(NL));
      }
    });

    return result.toString();
  }

}
