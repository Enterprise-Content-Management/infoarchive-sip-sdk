/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.IOException;
import java.io.InputStream;
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
import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Link;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNode;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;
import com.emc.ia.sdk.support.io.RuntimeIoException;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient {

  public static final String AUTH_TOKEN = "AuthToken";
  public static final String HOME_RESOURCE = "IAServer";
  public static final String APPLICATION_NAME = "Application";
  static final String LINK_AIPS = "http://identifiers.emc.com/aips";
  static final String LINK_INGEST = "http://identifiers.emc.com/ingest";
  static final String LINK_APPLICATIONS = "http://identifiers.emc.com/applications";
  static final String LINK_HOLDINGS = "http://identifiers.emc.com/holdings";
  static final String LINK_RECEIVER_NODES = "http://identifiers.emc.com/receiver-nodes";
  static final String LINK_TENANT = "http://identifiers.emc.com/tenant";

  private Tenant tenant;
  private Application application;
  private String aipsHref;
  private RestClient restClient = new RestClient(new HttpClient());

  /**
   * Configure InfoArchive server with given configuration parameters.
   * @param configuration The parameters to configure InfoArchive server
   */
  @Override
  public void configure(Map<String, String> configuration) {
    restClient.setHeaders(getHeaders(configuration.get("AuthToken")));
    try {
      setTenant(configuration.get("IAServer"));
      setApplication(configuration.get("Application"), configuration.get("Holding"));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
    setAipsHref();
  }

  /**
   * Ingests into InfoArchive server.
   * @param sip file to be ingested into InfoArchive server
   */
  @Override
  public String ingest(InputStream sip) throws IOException {
    ReceptionResponse response = restClient.ingest(aipsHref, sip, ReceptionResponse.class);
    Link ingestLink = response.getLinks().get(LINK_INGEST);
    IngestionResponse ingestionResponse = restClient.put(ingestLink.getHref(), IngestionResponse.class);
    return ingestionResponse.getAipId();
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
  //  result.getWorkingDirectory().setSubPath("reception");
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

  public void setRestClient(RestClient restClient) {
    this.restClient = restClient;
  }

}

