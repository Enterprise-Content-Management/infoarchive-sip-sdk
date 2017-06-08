/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.io.IOException;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.datetime.DefaultClock;
import com.opentext.ia.sdk.support.http.rest.RestClient;


/**
 * Configure InfoArchive using data in YAML format.
 */
public class YamlBasedConfigurer extends PropertiesBasedConfigurer {

  // NOTE: No IANA registration exists for YAML, but it seems most people use text/yaml
  private static final String MEDIA_YAML = "text/yaml";
  // TODO: Once the server supports this functionality, there will be a link relation somewhere that we can check
  private static final String LINK_YAML_CONFIGURATION = LINK_PREFIX + "configuration";

  private final YamlConfiguration yaml;

  public YamlBasedConfigurer(YamlConfiguration configuration) {
    this(configuration, null);
  }

  public YamlBasedConfigurer(YamlConfiguration configuration, RestClient restClient) {
    this(configuration, restClient, new DefaultClock());
  }

  public YamlBasedConfigurer(YamlConfiguration configuration, RestClient restClient, Clock clock) {
    super(null, restClient, clock);
    this.yaml = configuration;
    // TODO: We don't want to expand into properties when the server support YAML directly.
    //       IOW, we want to move this call into the else branch in applyConfiguration().
    setConfiguration(yaml.toMap());
  }

  @Override
  protected void applyConfiguration() throws IOException {
    if (serverSupportsYamlConfiguration()) {
      letServerApplyConfiguration();
    } else {
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
