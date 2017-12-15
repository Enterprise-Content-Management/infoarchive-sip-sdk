/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Locale;


public enum ObjectConfiguration {

  CREATE_OR_UPDATE("create or update", false, true, true),
  CREATE("create", false, true, false),
  USE_EXISTING("use existing", false, false, false),
  IGNORE("ignore", true, false, false);

  private final String representation;
  private final boolean mayCreate;
  private final boolean mayUpdate;
  private final boolean shouldIgnore;

  ObjectConfiguration(String representation, boolean shouldIgnore, boolean mayCreate, boolean mayUpdate) {
    this.representation = representation;
    this.shouldIgnore = shouldIgnore;
    this.mayCreate = mayCreate;
    this.mayUpdate = mayUpdate;
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

  /**
   * Returns whether the object should be ignored.
   * @return Whether the object should be ignored
   * @since 9.8.0
   */
  public boolean shouldIgnoreObject() {
    return shouldIgnore;
  }

  /**
   * Returns whether the object may be created.
   * @return Whether the object may be created
   * @deprecated Use {@linkplain #mayCreateObject()} instead.
   * @since 9.8.0
   */
  @Deprecated
  public boolean canConfigureObject() {
    return mayCreateObject();
  }

  /**
   * Returns whether the object may be created.
   * @return Whether the object may be created
   * @since 9.8.0
   */
  public boolean mayCreateObject() {
    return mayCreate;
  }

  /**
   * Returns whether the object may be updated.
   * @return Whether the object may be updated
   * @since 9.8.0
   */
  public boolean mayUpdateObject() {
    return mayUpdate;
  }

  @Override
  public String toString() {
    return representation;
  }

}
