/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


public final class ConfigurationPropertiesFactory {

  private static final int MAX_PROPERTIES_RESOURCES = 10;

  public static ConfigurationProperties newInstance(ResourceResolver resolver) {
    ConfigurationProperties parent = new ConfigurationProperties();
    for (int index = 0; index < ConfigurationPropertiesFactory.MAX_PROPERTIES_RESOURCES; index++) {
      try {
        parent = new ConfigurationProperties(resolver, String.format("%d.properties", index), parent);
      } catch (UnknownResourceException e) {
        break;
      }
    }
    try {
      return new ConfigurationProperties(resolver, "configuration.properties", parent);
    } catch (UnknownResourceException e) {
      return parent;
    }
  }

  private ConfigurationPropertiesFactory() {
    // Utility class
  }

}
