/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.opentext.ia.sdk.client.ArchiveClient;
import com.opentext.ia.sdk.client.dto.*;
import com.opentext.ia.sdk.client.rest.ApplicationIngestionResourcesCache;
import com.opentext.ia.sdk.client.rest.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.client.rest.InfoArchiveRestClient;
import com.opentext.ia.sdk.support.NewInstance;
import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.datetime.DefaultClock;
import com.opentext.ia.sdk.support.http.HttpClient;
import com.opentext.ia.sdk.support.http.apache.ApacheHttpClient;
import com.opentext.ia.sdk.support.io.RuntimeIoException;
import com.opentext.ia.sdk.support.rest.AuthenticationStrategy;
import com.opentext.ia.sdk.support.rest.LinkContainer;
import com.opentext.ia.sdk.support.rest.RestClient;


/**
 * Factory methods for creating ArchiveClient.
 */
public final class ArchiveClients {

  private ArchiveClients() {
    // Utility class
  }

  /**
   * Returns an ArchiveClient instance and configures the InfoArchive server that it communicates with.
   * @param configurer How to configure InfoArchive
   * @return An ArchiveClient
   */
  public static ArchiveClient configuringServerUsing(InfoArchiveConfigurer configurer) {
    return configuringServerUsing(configurer, null);
  }

  /**
   * Returns an ArchiveClient instance and configures the InfoArchive server that it communicates with.
   * @param configurer How to configure InfoArchive
   * @param restClient The REST client to use for communication with the server
   * @return An ArchiveClient
   */
  public static ArchiveClient configuringServerUsing(InfoArchiveConfigurer configurer, RestClient restClient) {
    return configuringServerUsing(configurer, restClient, null);
  }

  /**
   * Returns an ArchiveClient instance and configures the InfoArchive server that it communicates with.
   * @param configurer How to configure InfoArchive
   * @param optionalClient The REST client to use for communication with the server
   * @param optionalClock The clock to use
   * @return An ArchiveClient
   */
  public static ArchiveClient configuringServerUsing(InfoArchiveConfigurer configurer, RestClient optionalClient,
      Clock optionalClock) {
    ServerConfiguration serverConfiguration = configurer.getServerConfiguration();
    Clock clock = Optional.ofNullable(optionalClock).orElseGet(DefaultClock::new);
    RestClient client = Optional.ofNullable(optionalClient).orElseGet(
        () -> createRestClient(serverConfiguration, clock));
    configurer.configure();
    return usingAlreadyConfiguredServer(serverConfiguration, client, clock);
  }

  private static RestClient createRestClient(ServerConfiguration configuration, Clock clock) {
    HttpClient httpClient = NewInstance.of(configuration.getHttpClientClassName(),
        ApacheHttpClient.class.getName()).as(HttpClient.class);
    AuthenticationStrategy authentication = new AuthenticationStrategyFactory(configuration).getAuthenticationStrategy(
        () -> httpClient, () -> clock);
    RestClient result = new RestClient(httpClient);
    result.init(authentication);
    return result;
  }

  /**
   * Creates a new ArchiveClient instance without installing any artifacts in the archive.
   * @param configuration How to communicate with the InfoArchive Server
   * @param restClient The RestClient used to interact with the InfoArchive REST api.
   * @return An ArchiveClient
   */
  public static ArchiveClient usingAlreadyConfiguredServer(ServerConfiguration configuration, RestClient restClient) {
    return usingAlreadyConfiguredServer(configuration, restClient, new DefaultClock());
  }

  private static ArchiveClient usingAlreadyConfiguredServer(ServerConfiguration configuration, RestClient restClient,
      Clock clock) {
    return new InfoArchiveRestClient(restClient, appResourceCache(restClient, configuration), clock);
  }

  private static ApplicationIngestionResourcesCache appResourceCache(RestClient restClient,
      ServerConfiguration configuration) {
    try {
      ApplicationIngestionResourcesCache resourceCache = new ApplicationIngestionResourcesCache(
          configuration.getApplicationName());
      Services services = restClient.get(configuration.getBillboardUri(), Services.class);
      Tenant tenant = getTenant(restClient, services);
      Application application = getApplication(restClient, tenant, configuration.getApplicationName());
      cacheResourceUris(resourceCache, restClient, application);
      return resourceCache;
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private static Tenant getTenant(RestClient restClient, Services services) throws IOException {
    return Objects.requireNonNull(restClient.follow(services, InfoArchiveLinkRelations.LINK_TENANT, Tenant.class),
        "Tenant not found.");
  }

  private static Application getApplication(RestClient restClient, Tenant tenant, String applicationName)
      throws IOException {
    Applications applications = restClient.follow(tenant, InfoArchiveLinkRelations.LINK_APPLICATIONS,
        Applications.class);
    return Objects.requireNonNull(applications.byName(applicationName),
        "Application named " + applicationName + " not found.");
  }

  private static void cacheResourceUris(ApplicationIngestionResourcesCache resourceCache,
      RestClient restClient, Application application) throws IOException {
    Aics aics = restClient.follow(application, InfoArchiveLinkRelations.LINK_AICS, Aics.class);
    LinkContainer aips = restClient.follow(application, InfoArchiveLinkRelations.LINK_AIPS, LinkContainer.class);

    Map<String, String> dipResourceUriByAicName = new HashMap<>();
    aics.getItems()
        .forEach(aic -> dipResourceUriByAicName.put(aic.getName(), aic.getUri(InfoArchiveLinkRelations.LINK_DIP)));
    resourceCache.setDipResourceUriByAicName(dipResourceUriByAicName);
    resourceCache.setCiResourceUri(application.getUri(InfoArchiveLinkRelations.LINK_CI));
    resourceCache.setAipResourceUri(application.getUri(InfoArchiveLinkRelations.LINK_AIPS));
    resourceCache.setAipIngestDirectResourceUri(aips.getUri(InfoArchiveLinkRelations.LINK_INGEST_DIRECT));
  }

  /**
   * Creates a new ArchiveClient instance without installing any artifacts in the archive using the default RestClient.
   * @param serverConfiguration How to communicate with the server
   * @return An ArchiveClient
   */
  public static ArchiveClient usingAlreadyConfiguredServer(ServerConfiguration serverConfiguration) {
    Clock clock = new DefaultClock();
    RestClient restClient = createRestClient(serverConfiguration, clock);
    return usingAlreadyConfiguredServer(serverConfiguration, restClient, clock);
  }

}
