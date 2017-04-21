/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.ClientConfigurationFinder;
import com.emc.ia.sdk.sip.client.dto.*;
import com.emc.ia.sdk.sip.client.rest.ArchiveOperationsByApplicationResourceCache;
import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.sip.client.rest.InfoArchiveRestClient;
import com.emc.ia.sdk.support.NewInstance;
import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.DefaultClock;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.AuthenticationStrategy;
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.RestClient;


/**
 * Factory methods for creating ArchiveClient.
 */
public final class ArchiveClients {

  private ArchiveClients() { }

  /**
   * Installs, if necessary, the application and holding artifacts based on the details in the configuration map then
   * returns an ArchiveClient instance.
   * @param configuration The configuration map.
   * @return An ArchiveClient
   */
  public static ArchiveClient withPropertyBasedAutoConfiguration(Map<String, String> configuration) {
    return withPropertyBasedAutoConfiguration(configuration, Optional.empty());
  }

  /**
   * Installs, if necessary, the application and holding artifacts based on the details in the configuration map then
   * returns an ArchiveClient instance.
   * @param configuration The configuration map.
   * @param restClient The RestClient used to interact with the InfoArchive REST api.
   * @return An ArchiveClient
   */
  public static ArchiveClient withPropertyBasedAutoConfiguration(Map<String, String> configuration,
      RestClient restClient) {
    return withPropertyBasedAutoConfiguration(configuration, restClient, null);
  }

  public static ArchiveClient withPropertyBasedAutoConfiguration(Map<String, String> configuration,
      RestClient restClient, Clock clock) {
    return withPropertyBasedAutoConfiguration(configuration, Optional.ofNullable(restClient),
        Optional.ofNullable(clock));
  }

  private static ArchiveClient withPropertyBasedAutoConfiguration(Map<String, String> configuration,
      Optional<RestClient> potentialClient) {
    return withPropertyBasedAutoConfiguration(configuration, potentialClient, Optional.empty());
  }

  private static ArchiveClient withPropertyBasedAutoConfiguration(Map<String, String> configuration,
      Optional<RestClient> potentialClient, Optional<Clock> potentialClock) {
    Clock clock = potentialClock.orElseGet(DefaultClock::new);
    RestClient client = potentialClient.orElseGet(() -> createRestClient(configuration, clock));
    InfoArchiveConfigurers.propertyBased(configuration, client, clock).configure();
    String billboardUrl = getBillboadUrl(configuration);
    String applicationName = getApplicationName(configuration);
    return client(client, billboardUrl, applicationName);
  }

  /**
   * Creates a new ArchiveClient instance without installing any artifacts in the archive using the default RestClient.
   * @param billboardUrl The URL entry point to the InfoArchive REST api.
   * @param applicationName The name of the application to create the ArchiveClient for.
   * @return An ArchiveClient
   */
  public static ArchiveClient client(String billboardUrl, String applicationName) {
    RestClient restClient = createDefaultRestClient();
    return new InfoArchiveRestClient(restClient, appResourceCache(restClient, billboardUrl, applicationName));
  }

  /**
   * Creates a new ArchiveClient instance without installing any artifacts in the archive using the sdk configuration
   * file.
   * @return An ArchiveClient
   */
  public static ArchiveClient client() {
    Map<String, String> configuration = getSdkConfiguration();
    RestClient restClient = createRestClient(configuration, null);
    String billboardUrl = getBillboadUrl(configuration);
    String applicationName = getApplicationName(configuration);
    return new InfoArchiveRestClient(restClient, appResourceCache(restClient, billboardUrl, applicationName));
  }

  /**
   * Creates a new ArchiveClient instance without installing any artifacts in the archive.
   * @param configuration The configuration map.
   * @return An ArchiveClient
   */
  public static ArchiveClient client(Map<String, String> configuration) {
    RestClient restClient = createRestClient(configuration, null);
    String billboardUrl = getBillboadUrl(configuration);
    String applicationName = getApplicationName(configuration);
    return new InfoArchiveRestClient(restClient, appResourceCache(restClient, billboardUrl, applicationName));
  }

  /**
   * Creates a new ArchiveClient instance without installing any artifacts in the archive.
   * @param restClient The RestClient used to interact with the InfoArchive REST api.
   * @param billboardUrl The URL entry point to the InfoArchive REST api.
   * @param applicationName The name of the application to create the ArchiveClient for.
   * @return An ArchiveClient
   */
  public static ArchiveClient client(RestClient restClient, String billboardUrl, String applicationName) {
    return new InfoArchiveRestClient(restClient, appResourceCache(restClient, billboardUrl, applicationName));
  }

  private static ArchiveOperationsByApplicationResourceCache appResourceCache(RestClient restClient,
      String billboardUrl, String applicationName) {
    try {
      ArchiveOperationsByApplicationResourceCache resourceCache =
          new ArchiveOperationsByApplicationResourceCache(applicationName);
      Services services = restClient.get(billboardUrl, Services.class);
      Tenant tenant = getTenant(restClient, services);
      Application application = getApplication(restClient, tenant, applicationName);
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

  private static void cacheResourceUris(ArchiveOperationsByApplicationResourceCache resourceCache,
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

  private static RestClient createDefaultRestClient() {
    Map<String, String> configuration = getSdkConfiguration();
    return createRestClient(configuration, null);
  }

  private static Map<String, String> getSdkConfiguration() {
    try (InputStream stream = ClientConfigurationFinder.find()) {
      return asMap(stream);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private static Map<String, String> asMap(InputStream stream) throws IOException {
    Properties properties = new Properties();
    properties.load(stream);
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<Object, Object> e : properties.entrySet()) {
      result.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
    }
    return result;
  }

  private static RestClient createRestClient(Map<String, String> configuration, Clock clock) {
    HttpClient httpClient = NewInstance.fromConfiguration(configuration, HTTP_CLIENT_CLASSNAME,
        ApacheHttpClient.class.getName()).as(HttpClient.class);
    AuthenticationStrategy authentication = new AuthenticationStrategyFactory(configuration).getAuthenticationStrategy(
        () -> httpClient, () -> Optional.ofNullable(clock).orElseGet(DefaultClock::new));
    RestClient result = new RestClient(httpClient);
    result.init(authentication);
    return result;
  }

  private static String getApplicationName(Map<String, String> configuration) {
    String result = configuration.get(APPLICATION_NAME);
    if (result == null) {
      // Backwards compatibility
      result = configuration.get(OLD_APPLICATION_NAME);
    }
    return Objects.requireNonNull(result,
        "The property " + APPLICATION_NAME + " cannot be null or empty.");
  }

  private static String getBillboadUrl(Map<String, String> configuration) {
    return Objects.requireNonNull(configuration.get(SERVER_URI),
        "The property " + SERVER_URI + " cannot be null or empty.");
  }

}
