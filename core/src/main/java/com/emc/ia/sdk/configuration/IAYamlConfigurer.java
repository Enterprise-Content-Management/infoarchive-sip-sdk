package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Databases;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.sip.client.dto.Tenants;
import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public class IAYamlConfigurer implements IAConfigurer, InfoArchiveLinkRelations {

  private RestClient client;
  private YamlConfigurationFile configuration; //TODO: Maybe replace with final class in future

  private final IACache configurationState = new IACache();

  public IAYamlConfigurer(RestClient client, String servicesUri, YamlConfigurationFile configuration) throws IOException {
    this.client = client;
    this.configuration = configuration;
    configurationState.cacheOne(client.get(servicesUri, Services.class));
  }

  @Override
  public void configure() {
    try {
      ensureTenant();
      ensureApplication();
      ensureFederation();
      ensureDatabase();
      //TODO: split somehow in nearest future
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
  }

  @Override
  public ArchiveClient createArchiveClient() {
    throw new UnsupportedOperationException();
  }

  private void ensureTenant() throws IOException {
    Tenant tenant = configuration.getTenant(); //It seems that this tenant is different from
    Tenants tenants = client.follow(configurationState.getFirst(Services.class), LINK_TENANTS, Tenants.class);
    /*
    this one. Former one does not suppose to have any links except named ones.
     */
    Tenant createdTenant = tenants.byName(tenant.getName());
    if (createdTenant == null) {
      createdTenant = client.createCollectionItem(tenants, tenant, LINK_ADD, LINK_SELF);
    }
    configurationState.cacheOne(createdTenant);
  }

  private void ensureApplication() throws IOException {
    Application application = configuration.getApplication();
    Applications applications = client.follow(configurationState.getFirst(Tenant.class), LINK_APPLICATIONS, Applications.class);
    Application createdApplication = applications.byName(application.getName());
    if (createdApplication == null) {
      createdApplication = client.createCollectionItem(applications, application, LINK_ADD, LINK_SELF);
    }
    configurationState.cacheOne(createdApplication);
  }

  private void ensureFederation() throws IOException {
    Federation federation = configuration.getFederation();
    Federations federations = client.follow(configurationState.getFirst(Services.class), LINK_FEDERATIONS, Federations.class);
    Federation createdFederation = federations.byName(federation.getName());
    if (createdFederation == null) {
      createdFederation = client.createCollectionItem(federations, federation, LINK_ADD, LINK_SELF);
    }
    configurationState.cacheOne(createdFederation);
  }

  private void ensureDatabase() throws IOException {
    Database db = configuration.getDatabase();
    Databases databases = client.follow(configurationState.getFirst(Federation.class), LINK_DATABASES, Databases.class);
    Database createdDb = databases.byName(db.getName());
    if (createdDb == null) {
      createdDb = client.createCollectionItem(databases, db, LINK_ADD, LINK_SELF);
    }
    configurationState.cacheOne(createdDb);
  }

//  @Override
//  public IARemoteSnapshot snapshotConfiguration() {
//    throw new UnsupportedOperationException();
//  }
}
