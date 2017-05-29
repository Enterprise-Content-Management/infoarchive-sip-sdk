/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

import java.util.Map;

import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.DefaultClock;
import com.emc.ia.sdk.support.rest.RestClient;


/**
 * @deprecated Use {@link InfoArchiveConfigurers#propertyBased(Map, RestClient, Clock)} instead.
 */
@Deprecated
public class PropertyBasedConfigurer extends PropertiesBasedConfigurer {

  public PropertyBasedConfigurer(Map<String, String> configuration) {
    this(null, configuration);
  }

  public PropertyBasedConfigurer(RestClient restClient, Map<String, String> configuration) {
    this(restClient, new DefaultClock(), configuration);
  }

  public PropertyBasedConfigurer(RestClient restClient, Clock clock, Map<String, String> configuration) {
    super(restClient, clock, configuration);
  }

}
