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
import com.emc.ia.sdk.sip.ingestion.dto.Federation;
import com.emc.ia.sdk.sip.ingestion.dto.Federations;
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
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.MediaTypes;
import com.emc.ia.sdk.support.rest.RestClient;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient, InfoArchiveLinkRelations, InfoArchiveConfiguration {

  private final RestClient restClient;
  private Map<String, String> configuration;
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

  @Override
  public void configure(Map<String, String> config) {
    configuration = config;
    try {
      configureRestClient();
      ensureTenant();
      ensureFederation();
      ensureApplication();
      ensureSpace();
      ensureReceiverNode();
      ensureHolding();
      cacheIngestUri();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void configureRestClient() throws IOException {
    List<Header> headers = new ArrayList<Header>();
    headers.add(new BasicHeader("Authorization", "Bearer " + configuration.get(SERVER_AUTENTICATON_TOKEN)));
    headers.add(new BasicHeader("Accept", MediaTypes.HAL));
    restClient.setHeaders(headers);
    services = restClient.get(configured(SERVER_URI), Services.class);
  }

  public String configured(String name) {
    String result = configuration.get(name);
    Objects.requireNonNull(result, "Missing " + name);
    return result;
  }

  private void ensureTenant() throws IOException {
    tenant = restClient.follow(services, LINK_TENANT, Tenant.class);
  }

  private <T> T createItem(LinkContainer collection, T item) throws IOException {
    return createItem(collection, LINK_ADD, item, MediaTypes.JSON);
  }

  public <T> T createItem(LinkContainer collection, String addLinkRelation, T item, String mediaType)
      throws IOException {
    return restClient.createCollectionItem(collection, addLinkRelation, item, mediaType);
  }

  private void ensureFederation() throws IOException {
    String name = configuration.get(FEDERATION_NAME);
    Federations federations = restClient.follow(services, LINK_FEDERATIONS, Federations.class);
    Federation federation = federations.byName(name);
    if (federation == null) {
      createItem(services, LINK_FEDERATIONS, createFederation(name), MediaTypes.HAL);
    }
  }

  private void ensureApplication() throws IOException {
    String applicationName = configuration.get(APPLICATION_NAME);
    Applications applications = restClient.follow(tenant, LINK_APPLICATIONS, Applications.class);
    application = applications.byName(applicationName);
    if (application == null) {
      application = createItem(applications, createApplication(applicationName));
      Objects.requireNonNull(application, "Could not create application " + applicationName);
    }
  }

  private void ensureSpace() throws IOException {
    String spaceName = application.getName();
    Spaces spaces = restClient.follow(application, LINK_SPACES, Spaces.class);
    Space space = spaces.byName(spaceName);
    if (space == null) {
      createItem(spaces, LINK_ADD, createSpace(spaceName), MediaTypes.HAL);
    }
  }

  private void ensureReceiverNode() throws IOException {
    ReceiverNodes receiverNodes = restClient.follow(application, LINK_RECEIVER_NODES, ReceiverNodes.class);
    createItem(receiverNodes, createReceiverNode());
  }

  private void ensureHolding() throws IOException {
    String holdingName = configuration.get(HOLDING_NAME);
    Holdings holdings = restClient.follow(application, LINK_HOLDINGS, Holdings.class);
    createItem(holdings, createHolding(holdingName));
  }

  private Federation createFederation(String name) {
    Federation result = new Federation();
    result.setName(name);
    result.setSuperUserPassword(configuration.get(FEDERATION_SUPERUSER_PASSWORD));
    result.setBootstrap(configuration.get(FEDERATION_BOOTSTRAP));
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

  @Override
  public String ingest(InputStream sip) throws IOException {
    Objects.requireNonNull(ingestUri, "Did you forget to call configure()?");
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

}
