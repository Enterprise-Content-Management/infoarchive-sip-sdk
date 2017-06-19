/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import static com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.*;

import java.io.IOException;
import java.util.Map;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedConfigurer;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.yaml.configuration.YamlConfiguration;


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
    // Flatten the YAML to properties and re-use the properties-based configurer
    new PropertiesBasedConfigurer(yamlToMap(connection)).configure(connection);
  }

  private Map<String, String> yamlToMap(ArchiveConnection connection) {
    Map<String, String> properties = new YamlPropertiesMap(yaml.getMap());
    setConnectionProperties(connection, properties);
    properties.entrySet().stream()
        .filter(entry -> entry.getValue() != null)
        .map(entry -> entry.getKey() + " = " + entry.getValue())
        .sorted()
        .forEach(System.out::println);
    return properties;
  }

  private void setConnectionProperties(ArchiveConnection connection, Map<String, String> properties) {
    properties.put(HTTP_CLIENT_CLASSNAME, connection.getHttpClientClassName());
    properties.put(PROXY_HOST, connection.getProxyHost());
    properties.put(PROXY_PORT, connection.getProxyPort());
    properties.put(SERVER_AUTHENTICATION_GATEWAY, connection.getAuthenticationGateway());
    properties.put(SERVER_AUTHENTICATION_PASSWORD, connection.getAuthenticationPassword());
    properties.put(SERVER_AUTHENTICATION_TOKEN, connection.getAuthenticationToken());
    properties.put(SERVER_AUTHENTICATION_USER, connection.getAuthenticationUser());
    properties.put(SERVER_URI, connection.getBillboardUri());
    properties.put(SERVER_CLIENT_ID, connection.getClientId());
    properties.put(SERVER_CLIENT_SECRET, connection.getClientSecret());
  }

}
