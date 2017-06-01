/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.configurer;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.rest.RestClient;


/**
 * Configure InfoArchive using data in YAML format.
 */
public class YamlBasedConfigurer extends PropertiesBasedConfigurer {

  public YamlBasedConfigurer(YamlConfiguration configuration) {
    super(configuration.toMap());
  }

  public YamlBasedConfigurer(YamlConfiguration configuration, RestClient restClient) {
    super(configuration.toMap(), restClient);
  }

  public YamlBasedConfigurer(YamlConfiguration configuration, RestClient restClient, Clock clock) {
    super(configuration.toMap(), restClient, clock);
  }

}
