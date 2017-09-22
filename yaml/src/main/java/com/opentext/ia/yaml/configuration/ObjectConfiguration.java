/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Locale;


public enum ObjectConfiguration {

  CREATE_OR_UPDATE("create or update", true),
  CREATE("create", true),
  USE_EXISTING("use existing", false);

  private final String representation;
  private final boolean configureObject;

  ObjectConfiguration(String representation, boolean configureObject) {
    this.representation = representation;
    this.configureObject = configureObject;
  }

  public boolean canConfigureObject() {
    return configureObject;
  }

  public static ObjectConfiguration parse(String text) {
    if (text == null || text.isEmpty()) {
      return CREATE_OR_UPDATE;
    }
    String value = text.replace('_', ' ').toLowerCase(Locale.ENGLISH);
    // Support previous implementation with only two options
    if ("false".equals(value)) {
      return USE_EXISTING;
    }
    if ("true".equals(value)) {
      return CREATE_OR_UPDATE;
    }
    for (ObjectConfiguration result : values()) {
      if (result.representation.equals(value)) {
        return result;
      }
    }
    throw new IllegalArgumentException("Unknown ObjectConfiguration: " + text);
  }

  @Override
  public String toString() {
    return representation;
  }

}
