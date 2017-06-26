/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.dto.Services;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.rest.Link;
import com.opentext.ia.sdk.support.http.rest.RestClient;
import com.opentext.ia.sdk.test.TestCase;
import com.opentext.ia.sdk.yaml.configuration.YamlConfiguration;


public class WhenConfiguringServerUsingYaml extends TestCase implements InfoArchiveLinkRelations {

  private final HttpClient httpClient = mock(HttpClient.class);
  private final ArchiveConnection connection = new ArchiveConnection();
  private final ApplicationConfigurer clientSideConfigurer = mock(ApplicationConfigurer.class);
  private final YamlBasedConfigurer configurer = new YamlBasedConfigurer(
      new YamlConfiguration("application:\n  name: foo"), (yaml, conn) -> clientSideConfigurer);

  @Before
  @SuppressWarnings("unchecked")
  public void init() throws IOException {
    connection.setRestClient(new RestClient(httpClient));
  }

  @Test
  public void shouldDeferToServerWhenItSupportsYamlConfiguration() throws Exception {
    String configurationUri = randomUri();
    Services services = new Services();
    services.getLinks().put(LINK_CONFIGURATION, new Link(configurationUri));
    when(httpClient.get(anyString(), anyObject(), eq(Services.class))).thenReturn(services);

    configurer.configure(connection);

    verify(httpClient).put(eq(configurationUri), anyObject(), eq(String.class), anyString());
    verify(clientSideConfigurer, never()).configure(anyObject());
  }

  @Test
  public void shouldConfigureFromClientWhenServerDoesntSupportsYamlConfiguration() throws Exception {
    Services services = new Services();
    when(httpClient.get(anyString(), anyObject(), eq(Services.class))).thenReturn(services);

    configurer.configure(connection);

    verify(clientSideConfigurer).configure(anyObject());
    verify(httpClient, never()).put(anyString(), anyObject(), anyObject(), anyString());
  }

}
