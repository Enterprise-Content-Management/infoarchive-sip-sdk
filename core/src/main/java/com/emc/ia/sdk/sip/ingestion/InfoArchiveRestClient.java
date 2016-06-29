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
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.message.BasicHeader;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Applications;
import com.emc.ia.sdk.sip.ingestion.dto.Databases;
import com.emc.ia.sdk.sip.ingestion.dto.Federation;
import com.emc.ia.sdk.sip.ingestion.dto.Holding;
import com.emc.ia.sdk.sip.ingestion.dto.Holdings;
import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNode;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Services;
import com.emc.ia.sdk.sip.ingestion.dto.Space;
import com.emc.ia.sdk.sip.ingestion.dto.Spaces;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.Link;
import com.emc.ia.sdk.support.rest.MediaTypes;
import com.emc.ia.sdk.support.rest.RestClient;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient, InfoArchiveLinkRelations {

  public static final String AUTH_TOKEN = "AuthToken";
  public static final String HOME_RESOURCE = "IAServer";
  public static final String APPLICATION_NAME = "Application";

  private final RestClient restClient;
  private Services services;
  private Tenant tenant;
  private Application application;
  private String ingestUri;

  public InfoArchiveRestClient() {
    this(new RestClient());
  }

  public InfoArchiveRestClient(RestClient restClient) {
    this.restClient = restClient;
  }

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
    cacheIngestUri();
  }

  /**
   * Ingests into InfoArchive server.
   * @param sip file to be ingested into InfoArchive server
   */
  @Override
  public String ingest(InputStream sip) throws IOException {
    ReceptionResponse response = ingest(ingestUri, sip, ReceptionResponse.class);
    Link ingestLink = response.getLinks().get(LINK_INGEST);
    IngestionResponse ingestionResponse = restClient.put(ingestLink.getHref(), IngestionResponse.class);
    return ingestionResponse.getAipId();
  }

  private <T> T ingest(String uri, InputStream sip, Class<T> type) throws IOException {
    // TODO - what should be the file name here ? IASIP.zip is Ok ?
    InputStreamBody file = new InputStreamBody(sip, ContentType.APPLICATION_OCTET_STREAM, "IASIP.zip");
    HttpEntity entity = MultipartEntityBuilder.create()
        .addTextBody("format", "sip_zip")
        .addPart("sip", file)
        .build();
    return restClient.post(uri, restClient.getHeaders(), entity, type);
  }

  private List<Header> getHeaders(String authToken) {
    List<Header> result = new ArrayList<Header>();
    result.add(new BasicHeader("Authorization", "Bearer " + authToken));
    result.add(new BasicHeader("Accept", "application/hal+json"));
    return result;
  }

  private void setTenant(String billboardUri) throws IOException {
    Objects.requireNonNull(billboardUri, "Missing billboard URI");
    services = restClient.get(billboardUri, Services.class);
    tenant = restClient.follow(services, LINK_TENANT, Tenant.class);
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
    Federation federation = restClient.createCollectionItem(services, LINK_FEDERATIONS,
        createFederation(applicationName), MediaTypes.HAL);
    restClient.follow(federation, LINK_DATABASES, Databases.class);
    // TODO: Create database

    application = restClient.createCollectionItem(applications, LINK_ADD, createApplication(applicationName));
    Objects.requireNonNull(application, "Could not create application " + applicationName);

    Spaces spaces = restClient.follow(application, LINK_SPACES, Spaces.class);
    restClient.createCollectionItem(spaces, LINK_ADD, createSpace(applicationName));

    ReceiverNodes receiverNodes = restClient.follow(application, LINK_RECEIVER_NODES, ReceiverNodes.class);
    restClient.createCollectionItem(receiverNodes, LINK_ADD, createReceiverNode());

    Holdings holdings = restClient.follow(application, LINK_HOLDINGS, Holdings.class);
    restClient.createCollectionItem(holdings, LINK_ADD, createHolding(holdingName));
  }

  private Federation createFederation(String name) {
    Federation result = new Federation();
    result.setName(name);
    // TODO: Get these from configuration
    result.setSuperUserPassword("test");
    result.setBootstrap("xhive://127.0.0.1:2910");
    return result;
  }

  private Space createSpace(String name) {
    Space result = new Space();
    result.setName(name);
    return result;
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

  private void cacheIngestUri() {
    ingestUri = application.getUri(LINK_AIPS);
  }

}
