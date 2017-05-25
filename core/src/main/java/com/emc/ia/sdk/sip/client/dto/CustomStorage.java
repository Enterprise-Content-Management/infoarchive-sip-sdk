/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import java.util.Map;


public class CustomStorage extends NamedLinkContainer {

  private String description;
  private Map<String, String> properties;
  private String factoryServiceName;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  public void addProperty(String key, String value) {
    if (key != null && value != null) {
      this.properties.put(key, value);
    }
  }

  public String getFactoryServiceName() {
    return factoryServiceName;
  }

  public void setFactoryServiceName(String factoryServiceName) {
    this.factoryServiceName = factoryServiceName;
  }

}
