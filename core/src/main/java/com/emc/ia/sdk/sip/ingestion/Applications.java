/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Applications extends LinkContainer {

  private static final String KEY = "applications";

  @JsonProperty("_embedded")
  private Map<String, List<Application>> applicationsByName = new HashMap<String, List<Application>>();

  public Application byName(String name) {
    if (applicationsByName.containsKey(KEY)) {
      for (Application app : applicationsByName.get(KEY)) {
        if (name.equals(app.getName())) {
          return app;
        }
      }
    }
    return null;
  }

  protected void setApplications(Map<String, List<Application>> applications) {
    this.applicationsByName = applications;
  }

}
