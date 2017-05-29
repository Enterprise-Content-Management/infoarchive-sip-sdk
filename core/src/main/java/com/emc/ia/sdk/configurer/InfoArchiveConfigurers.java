/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

import java.util.Map;

import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.DefaultClock;
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
    return propertyBased(configuration, restClient, new DefaultClock());
  }

  public static InfoArchiveConfigurer propertyBased(Map<String, String> configuration, RestClient restClient,
      Clock clock) {
    return new PropertiesBasedConfigurer(restClient, clock, configuration);
  }

}
