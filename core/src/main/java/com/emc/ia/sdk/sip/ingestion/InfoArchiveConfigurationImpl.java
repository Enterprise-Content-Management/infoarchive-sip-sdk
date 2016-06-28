/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Applications;
import com.emc.ia.sdk.sip.ingestion.dto.Holding;
import com.emc.ia.sdk.sip.ingestion.dto.Holdings;
import com.emc.ia.sdk.sip.ingestion.dto.HomeResource;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNode;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;
import com.emc.ia.sdk.support.io.RuntimeIoException;


/**
 * Implementation of {@linkplain InfoArchiveConfiguration} that configures a running InfoArchive server.
 */
public class InfoArchiveConfigurationImpl implements InfoArchiveConfiguration {

  //TODO - are these URLs exposed anywhere through properties ?
  private static final String LINK_AIPS = "http://identifiers.emc.com/aips";
  private static final String LINK_TENANT = "http://identifiers.emc.com/tenant";
  private static final String LINK_APPLICATIONS = "http://identifiers.emc.com/applications";
  private static final String LINK_HOLDINGS = "http://identifiers.emc.com/holdings";
  private static final String LINK_RECEIVER_NODES = "http://identifiers.emc.com/receiver-nodes";

  private final RestClient restClient;
  private Tenant tenant;
  private Application application;
  private String aipsHref;

  public <T> InfoArchiveConfigurationImpl(Map<String, String> configuration, RestClient restClient) {
    //Map contains 3 keys ; "AuthToken" , "IAServer" , "Application" - Extract this information
    //TODO - Are these keys standardized somewhere ?
    //TODO - safety check, logging OR return ?
    this.restClient = restClient;
    restClient.setHeaders(getHeaders(configuration.get("AuthToken")));
    try {
      setTenant(configuration.get("IAServer"));
      setApplication(configuration.get("Application"), configuration.get("Holding"));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    setAipsHref();
  }

  private List<Header> getHeaders(String authToken) {
    List<Header> result = new ArrayList<Header>();
    result.add(new BasicHeader("Authorization", "Bearer " + authToken));
    result.add(new BasicHeader("Accept", "application/hal+json"));
    return result;
  }

  private void setTenant(String billboardUri) throws IOException {
    Objects.requireNonNull(billboardUri, "Missing billboard URI");
    HomeResource homeResource = restClient.get(billboardUri, HomeResource.class);
    tenant = restClient.follow(homeResource, LINK_TENANT, Tenant.class);
  }

  private void setApplication(String applicationName, String holdingName) throws IOException {
    Applications applications = restClient.follow(tenant, LINK_APPLICATIONS, Applications.class);
    application = applications.byName(applicationName);
    if (application == null) {
      addApplication(applications, applicationName, holdingName);
    }
  }

  private void addApplication(Applications applications, String applicationName, String holdingName)
      throws IOException {
    restClient.createCollectionItem(applications, createApplication(applicationName));
    application = restClient.refresh(applications)
        .byName(applicationName);
    Objects.requireNonNull(application, "Could not create application " + applicationName);

    ReceiverNodes receiverNodes = restClient.follow(application, LINK_RECEIVER_NODES, ReceiverNodes.class);
    restClient.createCollectionItem(receiverNodes, createReceiverNode());

    Holdings holdings = restClient.follow(application, LINK_HOLDINGS, Holdings.class);
    restClient.createCollectionItem(holdings, createHolding(holdingName));
  }

  private Application createApplication(String applicationName) {
    Application result = new Application();
    result.setName(applicationName);
    return result;
  }

  private ReceiverNode createReceiverNode() {
    ReceiverNode result = new ReceiverNode();
    result.setName("receiver_node_01");
    result.getWorkingDirectory().setName("reception");
//    result.getWorkingDirectory().setSubPath("reception");
    return result;
  }

  private Holding createHolding(String holdingName) {
    Holding result = new Holding();
    result.setName(holdingName);
    // TODO: Add PDI schema, indexes, etc.
    return result;
  }

  private void setAipsHref() {
    aipsHref = application.getLinks().get(LINK_AIPS).getHref();
  }

  @Override
  public List<Header> getHeaders() {
    return restClient.getHeaders();
  }

  @Override
  public Tenant getTenant() {
    return tenant;
  }

  @Override
  public Application getApplication() {
    return application;
  }

  @Override
  public String getAipsHref() {
    return aipsHref;
  }

}
