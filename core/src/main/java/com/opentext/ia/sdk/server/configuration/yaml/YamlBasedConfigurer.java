/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.io.IOException;

import com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedConfigurer;


/**
 * Configure an InfoArchive application using data in YAML format. This class also supports configuring more than one
 * application or non-application objects <em>provided the InfoArchive server supports this</em>, i.e. version 16.3 or
 * newer.
 */
public class YamlBasedConfigurer extends PropertiesBasedConfigurer {

  // NOTE: No IANA registration exists for YAML, but it seems most people use text/yaml
  private static final String MEDIA_YAML = "text/yaml";
  // TODO: Once the server supports this functionality, there will be a link relation somewhere that we can check
  private static final String LINK_YAML_CONFIGURATION = LINK_PREFIX + "configuration";

  private final YamlConfiguration yaml;

  public YamlBasedConfigurer(YamlConfiguration configuration) {
    super(null);
    this.yaml = configuration;
  }

  @Override
  protected void applyConfiguration() throws IOException {
    if (serverSupportsYamlConfiguration()) {
      letServerApplyConfiguration();
    } else {
      setConfiguration(yaml.toMap());
      super.applyConfiguration();
    }
  }

  private boolean serverSupportsYamlConfiguration() {
    return getConfigurationUri() != null;
  }

  private String getConfigurationUri() {
    return getCache().getServices().getUri(LINK_YAML_CONFIGURATION);
  }

  private void letServerApplyConfiguration() throws IOException {
    getRestClient().put(getConfigurationUri(), String.class, yaml.toString(), MEDIA_YAML);
  }

}
