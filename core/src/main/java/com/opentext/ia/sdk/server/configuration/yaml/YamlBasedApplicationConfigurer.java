/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.HTTP_CLIENT_CLASSNAME;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.PROXY_HOST;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.PROXY_PORT;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.SERVER_AUTHENTICATION_GATEWAY;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.SERVER_AUTHENTICATION_PASSWORD;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.SERVER_AUTHENTICATION_TOKEN;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.SERVER_AUTHENTICATION_USER;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.SERVER_CLIENT_ID;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.SERVER_CLIENT_SECRET;
import static com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties.SERVER_URI;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.yaml.configuration.YamlConfiguration;

/**
 * Configure an InfoArchive application using data in YAML format. This class also supports configuring more than one
 * application or non-application objects <em>provided the InfoArchive server supports this</em> (version 16EP3 or
 * newer).
 * <dl>
 * <dt>Warning:</dt>
 * <dd>Support for client-side processing of declarative configuration will be removed in a future version.</dd>
 * </dl>
 * @since 6.0.0
 */
@SuppressWarnings("deprecation")
public class YamlBasedApplicationConfigurer implements ApplicationConfigurer {

  private final YamlConfiguration yaml;
  private final BiFunction<YamlConfiguration, ArchiveConnection, ApplicationConfigurer> clientSideApplicationConfigurerFactory;

  public YamlBasedApplicationConfigurer(YamlConfiguration configuration) {
    this(configuration, YamlBasedApplicationConfigurer::defaultClientSideApplicationConfigurer);
  }

  private static ApplicationConfigurer defaultClientSideApplicationConfigurer(YamlConfiguration yaml,
      ArchiveConnection connection) {
    // TODO: Remove support for client-side processing of declarative configuration
    // Flatten the YAML to properties and re-use the properties-based configurer
    return new com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedApplicationConfigurer(
        yamlToMap(yaml, connection));
  }

  private static Map<String, String> yamlToMap(YamlConfiguration yaml, ArchiveConnection connection) {
    Map<String, String> result = new YamlPropertiesMap(yaml.getMap());
    setConnectionProperties(connection, result);
    return result;
  }

  private static void setConnectionProperties(ArchiveConnection connection, Map<String, String> properties) {
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

  YamlBasedApplicationConfigurer(YamlConfiguration configuration,
      BiFunction<YamlConfiguration, ArchiveConnection, ApplicationConfigurer> clientSideApplicationConfigurerFactory) {
    this.yaml = configuration;
    this.clientSideApplicationConfigurerFactory = clientSideApplicationConfigurerFactory;
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

  private void applyConfigurationFromClient(ArchiveConnection connection) throws IOException {
    clientSideApplicationConfigurerFactory.apply(yaml, connection).configure(connection);
  }

}
