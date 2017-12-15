/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration.json;

import com.opentext.ia.configuration.ConfigurationObject;
import com.opentext.ia.configuration.ConfigurationProducer;


/**
 * Produce an InfoArchive configuration in JSON format.
 * @author Ray Sinnema
 * @since 9.9.0
 */
public class JsonConfigurationProducer implements ConfigurationProducer<JsonConfiguration> {

  @Override
  public JsonConfiguration produce(ConfigurationObject container) {
    return new JsonConfiguration(container);
  }

}
