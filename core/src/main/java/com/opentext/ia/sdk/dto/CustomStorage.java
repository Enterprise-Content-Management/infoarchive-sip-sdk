/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;


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
    this.properties = SerializationUtils.clone(new HashMap<>(properties));
  }

  public String getFactoryServiceName() {
    return factoryServiceName;
  }

  public void setFactoryServiceName(String factoryServiceName) {
    this.factoryServiceName = factoryServiceName;
  }

}
