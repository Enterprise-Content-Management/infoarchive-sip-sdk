/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.client.impl.ApplicationIngestionResourcesCache;
import com.opentext.ia.sdk.client.impl.InfoArchiveRestClient;
import com.opentext.ia.sdk.dto.Aics;
import com.opentext.ia.sdk.dto.Application;
import com.opentext.ia.sdk.dto.Applications;
import com.opentext.ia.sdk.dto.ProductInfo;
import com.opentext.ia.sdk.dto.Services;
import com.opentext.ia.sdk.dto.Tenant;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.support.http.rest.LinkContainer;
import com.opentext.ia.sdk.support.http.rest.RestClient;


/**
 * Factory methods for creating {@linkplain ArchiveClient}s.
 */
public final class ArchiveClients {

  private ArchiveClients() {
    // Utility class
  }

  /**
   * Returns an {@linkplain ArchiveClient} for an application that it first configures.
   * @param configurer How to configure the InfoArchive application
   * @param connection How to communicate with the InfoArchive server
   * @return An ArchiveClient
   * @throws IOException When an I/O error occurs
   */
  public static ArchiveClient configuringApplicationUsing(ApplicationConfigurer configurer,
      ArchiveConnection connection) throws IOException {
    configurer.configure(connection);
    return usingAlreadyConfiguredApplication(configurer.getApplicationName(), connection);
  }

  /**
   * Returns an {@linkplain ArchiveClient} for an already configured application.
   * @param applicationName The name of the already configured application to use
   * @param connection How to communicate with the InfoArchive server
   * @return An ArchiveClient
   * @throws IOException When an I/O error occurs
   */
  public static ArchiveClient usingAlreadyConfiguredApplication(String applicationName, ArchiveConnection connection)
      throws IOException {
    RestClient restClient = connection.getRestClient();
    return new InfoArchiveRestClient(restClient, appResourceCache(applicationName, connection, restClient));
  }

  private static ApplicationIngestionResourcesCache appResourceCache(String applicationName,
      ArchiveConnection connection, RestClient restClient) throws IOException {
    ApplicationIngestionResourcesCache resourceCache = new ApplicationIngestionResourcesCache(applicationName);
    Services services = connection.getServices();
    ProductInfo productInfo = getProductInfo(restClient, services);
    Tenant tenant = getTenant(restClient, services);
    Application application = getApplication(restClient, tenant, applicationName);
    cacheResourceUris(restClient, productInfo, application, resourceCache);
    return resourceCache;
  }

  private static ProductInfo getProductInfo(RestClient restClient, Services services) throws IOException {
    return Objects.requireNonNull(restClient.follow(services, InfoArchiveLinkRelations.LINK_PRODUCT_INFO,
        ProductInfo.class), "Product Info not found.");
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
        "Application " + applicationName + " not found.");
  }

  private static void cacheResourceUris(RestClient restClient, ProductInfo productInfo, Application application,
      ApplicationIngestionResourcesCache resourceCache) throws IOException {
    resourceCache.setServerVersion(productInfo.getBuildProperties().getServerVersion());

    Map<String, String> dipResourceUrisByAicName = new HashMap<>();
    Aics aics = restClient.follow(application, InfoArchiveLinkRelations.LINK_AICS, Aics.class);
    aics.getItems().forEach(aic ->
      dipResourceUrisByAicName.put(aic.getName(), aic.getUri(InfoArchiveLinkRelations.LINK_DIP)));
    resourceCache.setDipResourceUriByAicName(dipResourceUrisByAicName);

    resourceCache.setCiResourceUri(application.getUri(InfoArchiveLinkRelations.LINK_CI));
    resourceCache.setAipResourceUri(application.getUri(InfoArchiveLinkRelations.LINK_AIPS));
    LinkContainer aips = restClient.follow(application, InfoArchiveLinkRelations.LINK_AIPS, LinkContainer.class);
    resourceCache.setAipIngestDirectResourceUri(aips.getUri(InfoArchiveLinkRelations.LINK_INGEST_DIRECT));
  }

}
