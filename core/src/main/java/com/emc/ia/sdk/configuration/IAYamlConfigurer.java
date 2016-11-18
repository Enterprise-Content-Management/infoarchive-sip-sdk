package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Tenants;
import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.sip.client.rest.RestCache;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public class IAYamlConfigurer implements IAConfigurer, InfoArchiveLinkRelations {

  private RestClient client;
  private YamlConfigurationFile configuration; //TODO: Maybe replace with final class in future

  private final RestCache configurationState = new RestCache(); //TODO: replace with more convenient structure

  public IAYamlConfigurer(RestClient client, String servicesUri, YamlConfigurationFile configuration) throws IOException {
    this.client = client;
    this.configuration = configuration;
    configurationState.setServices(client.get(servicesUri, Services.class));
  }

  @Override
  public void configure() {
    try {
      String tenantName = configuration.getTenant().getName();
      Tenants tenants = client.follow(configurationState.getServices(), LINK_TENANTS, Tenants.class);
      configurationState.setTenant(tenants.byName(tenantName));
      if (configurationState.getTenant() == null) {
        configurationState.setTenant(client.createCollectionItem(tenants, configuration.getTenant(), LINK_ADD, LINK_SELF));
      }

      //TODO: Split construction

      String applicationName = configuration.getApplication().getName();
      Applications applications = client.follow(configurationState.getTenant(), LINK_APPLICATIONS, Applications.class);
      configurationState.setApplication(applications.byName(applicationName));
      if (configurationState.getApplication() == null) {
        configurationState.setApplication(client.createCollectionItem(applications, configuration.getApplication(), LINK_ADD, LINK_SELF));
      }

      String federationName = configuration.getFederation().getName();
      Federations federations = client.follow(configurationState.getServices(), LINK_FEDERATIONS, Federations.class);
      configurationState.setFederation(federations.byName(federationName));
      if (configurationState.getFederation() == null) {
        configurationState.setFederation(client.createCollectionItem(federations, configuration.getFederation(), LINK_ADD, LINK_SELF));
      }
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
  }

  @Override
  public ArchiveClient createArchiveClient() {
    throw new UnsupportedOperationException();
  }

//  @Override
//  public IARemoteSnapshot snapshotConfiguration() {
//    throw new UnsupportedOperationException();
//  }
}
