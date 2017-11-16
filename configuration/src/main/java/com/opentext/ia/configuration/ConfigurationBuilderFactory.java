/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public final class ConfigurationBuilderFactory {

  private final ConfigurationWriter manager;

  public ConfigurationBuilderFactory(ConfigurationWriter manager) {
    this.manager = manager;
  }

  public ConfigurationBuilder newConfiguration() {
    return new ConfigurationBuilder(manager);
  }

}
