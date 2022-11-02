/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.dto.Services;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.rest.Link;
import com.opentext.ia.sdk.support.http.rest.RestClient;
import com.opentext.ia.test.TestCase;

@Disabled
class WhenConfiguringServerUsingYaml extends TestCase implements InfoArchiveLinkRelations {

  private final HttpClient httpClient = mock(HttpClient.class);
  private final ArchiveConnection connection = new ArchiveConnection();
  private final ApplicationConfigurer clientSideConfigurer = mock(ApplicationConfigurer.class);

  @BeforeEach
  public void init() throws IOException {
    connection.setRestClient(new RestClient(httpClient));
  }

  @Test
  void shouldDeferToServerWhenItSupportsYamlConfiguration() throws IOException {
    String configurationUri = randomUri();
    Services services = new Services();
    services.getLinks().put(LINK_CONFIGURATION, new Link(configurationUri));
    when(httpClient.get(any(), any(), eq(Services.class))).thenReturn(services);

//    configurer.configure(connection);

    verify(httpClient).put(eq(configurationUri), any(), eq(String.class), anyString());
    verify(clientSideConfigurer, never()).configure(any());
  }

  @Test
  void shouldConfigureFromClientWhenServerDoesntSupportsYamlConfiguration() throws IOException {
    Services services = new Services();
    when(httpClient.get(any(), any(), eq(Services.class))).thenReturn(services);

//    configurer.configure(connection);

    verify(clientSideConfigurer).configure(any());
    verify(httpClient, never()).put(anyString(), any(), any(), anyString());
  }

}
