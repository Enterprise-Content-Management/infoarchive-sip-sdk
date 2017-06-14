/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.io.IOException;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedConfigurer;
import com.opentext.ia.sdk.support.http.MediaTypes;


/**
 * Configure an InfoArchive application using data in YAML format. This class also supports configuring more than one
 * application or non-application objects <em>provided the InfoArchive server supports this</em> (version 16.3 or
 * newer).
 * @since 6.0.0
 */
public class YamlBasedConfigurer implements ApplicationConfigurer {

  private final YamlConfiguration yaml;

  public YamlBasedConfigurer(YamlConfiguration configuration) {
    this.yaml = configuration;
  }

  @Override
  public String getApplicationName() {
    return yaml.getApplicationName();
  }

  @Override
  public void configure(ArchiveConnection connection) throws IOException {
    if (serverSupportsYamlConfiguration(connection)) {
      letServerApplyConfiguration(connection);
    } else {
      applyConfigurationFromClient(connection);
    }
  }

  private boolean serverSupportsYamlConfiguration(ArchiveConnection connection) throws IOException {
    return getConfigurationUri(connection) != null;
  }

  private String getConfigurationUri(ArchiveConnection connection) throws IOException {
    return connection.getServices().getUri(InfoArchiveLinkRelations.LINK_CONFIGURATION);
  }

  private void letServerApplyConfiguration(ArchiveConnection connection) throws IOException {
    connection.getRestClient().put(getConfigurationUri(connection), String.class, yaml.toString(), MediaTypes.YAML);
  }

  private void applyConfigurationFromClient(ArchiveConnection connection) {
    // Flatten the YAML to a properties map and re-use the properties-based configurer
    new PropertiesBasedConfigurer(yaml.toMap()).configure(connection);
  }

}
