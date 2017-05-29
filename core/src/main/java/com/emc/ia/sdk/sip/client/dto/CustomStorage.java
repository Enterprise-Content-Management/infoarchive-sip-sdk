/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import java.util.HashMap;
import java.util.Map;


public class CustomStorage extends NamedLinkContainer {

  private String description;
  private Map<String, String> properties = new HashMap<>();
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

  public String getFactoryServiceName() {
    return factoryServiceName;
  }

  public void setFactoryServiceName(String factoryServiceName) {
    this.factoryServiceName = factoryServiceName;
  }

}
