package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.configuration.artifacts.ApplicationIaHandler;
import com.emc.ia.sdk.configuration.artifacts.FederationIaHandler;
import com.emc.ia.sdk.configuration.artifacts.TenantIaHandler;
import com.emc.ia.sdk.configuration.artifacts.XdbDatabaseIaHandler;
import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Databases;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public final class IAYamlConfigurer implements IAConfigurer, InfoArchiveLinkRelations {

  private RestClient client;
  private YamlConfigurationFile configuration; //TODO: Maybe replace with final field in future

  private final IACache cache = new IACache();

  public IAYamlConfigurer(RestClient client, String servicesUri, YamlConfigurationFile configuration) throws IOException {
    this.client = client;
    this.configuration = configuration;
    cache.cacheOne(client.get(servicesUri, Services.class));
  }

  @Override
  public void configure() {
    try {
      ensureTenant();
      ensureApplication();
      ensureFederation();
      ensureDatabase();
      ensureFileSystemRoot();
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
  }

  @Override
  public ArchiveClient createArchiveClient() {
    throw new UnsupportedOperationException();
  }

  private void ensureTenant() throws IOException {
    BaseIAArtifact tenantArtifact = configuration.extractWith(TenantIaHandler.extractor());
    tenantArtifact.install(client, cache);
  }

  private void ensureApplication() throws IOException {
    BaseIAArtifact applicationArtifact = configuration.extractWith(ApplicationIaHandler.extractor());
    applicationArtifact.install(client, cache);
  }

  private void ensureFederation() throws IOException {
    BaseIAArtifact federationArtifact = configuration.extractWith(FederationIaHandler.extractor());
    federationArtifact.install(client, cache);
  }

  private void ensureDatabase() throws IOException {
    BaseIAArtifact databaseArtifact = configuration.extractWith(XdbDatabaseIaHandler.extractor());
    databaseArtifact.install(client, cache);
  }

  private void ensureFileSystemRoot() throws IOException {
    BaseIAArtifact fsRootArtifact = configuration.extractWith(XdbDatabaseIaHandler.extractor());
    fsRootArtifact.install(client, cache);
  }
}
