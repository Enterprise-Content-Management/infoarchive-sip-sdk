/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

import java.util.Map;

import com.emc.ia.sdk.support.rest.RestClient;

/**
 * Factory methods for creating InfoArchiveConfigurers.
 */
public final class InfoArchiveConfigurers {

  private InfoArchiveConfigurers() {
  }

  public static InfoArchiveConfigurer propertyBased(Map<String, String> configuration) {
    return propertyBased(configuration, null);
  }

  public static InfoArchiveConfigurer propertyBased(Map<String, String> configuration, RestClient restClient) {
    return new PropertyBasedConfigurer(restClient, configuration);
  }
}
