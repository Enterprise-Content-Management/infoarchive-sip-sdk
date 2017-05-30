/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support;

import java.util.Map;

/**
 * Create instances of a configured class.
 */
public class NewInstance {

  public static NewInstance fromConfiguration(Map<String, String> configuration, String typeConfigurationName,
      String defaultTypeName) {
    return new NewInstance(configuration, typeConfigurationName, defaultTypeName);
  }

  private final String className;

  public NewInstance(Map<String, String> configuration, String classConfigurationName, String defaultClassName) {
    className = configuration.getOrDefault(classConfigurationName, defaultClassName);
  }

  public <T> T as(Class<T> type) {
    try {
      return type.cast(Class.forName(className)
        .newInstance());
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
