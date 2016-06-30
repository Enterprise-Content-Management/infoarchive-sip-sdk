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
import com.emc.ia.sdk.sip.ingestion.dto.Database;
import com.emc.ia.sdk.sip.ingestion.dto.Databases;
import com.emc.ia.sdk.sip.ingestion.dto.Federation;
import com.emc.ia.sdk.sip.ingestion.dto.Federations;
import com.emc.ia.sdk.sip.ingestion.dto.FileSystemFolder;
import com.emc.ia.sdk.sip.ingestion.dto.FileSystemFolders;
import com.emc.ia.sdk.sip.ingestion.dto.FileSystemRoots;
import com.emc.ia.sdk.sip.ingestion.dto.Holding;
import com.emc.ia.sdk.sip.ingestion.dto.Holdings;
import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNode;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Services;
import com.emc.ia.sdk.sip.ingestion.dto.Sip;
import com.emc.ia.sdk.sip.ingestion.dto.Space;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootFolder;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootFolders;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootLibraries;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootLibrary;
import com.emc.ia.sdk.sip.ingestion.dto.Spaces;
import com.emc.ia.sdk.sip.ingestion.dto.Store;
import com.emc.ia.sdk.sip.ingestion.dto.Stores;
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

  private static final String WORKING_FOLDER_NAME = "working/";
  private static final String RECEIVER_NODE_NAME = "receiver_node_01";
  private static final String STORE_NAME = "filestore_01";
  private final RestClient restClient;
  private Map<String, String> configuration;
  private Services services;
  private Tenant tenant;
  private Application application;
  private String ingestUri;
  private Federation federation;
  private String fileSystemRootUri;
  private Space space;
  private String databaseUri;
  private SpaceRootFolder spaceRootFolder;
  private String fileSystemFolderUri;
  private String receptionFolderUri;
  private String storeUri;

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
      ensureDatabase();
      ensureFileSystemRoot();
      ensureApplication();
      ensureSpace();
      ensureSpaceRootLibrary();
      ensureSpaceRootFolder();
      ensureFileSystemFolder();
      ensureStore();
      ensureReceptionFolder();
      ensureIngestionFolder();
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
    return createItem(collection, LINK_ADD, item);
  }

  public <T> T createItem(LinkContainer collection, String addLinkRelation, T item)
      throws IOException {
    return restClient.createCollectionItem(collection, addLinkRelation, item);
  }

  private void ensureFederation() throws IOException {
    String name = configuration.get(FEDERATION_NAME);
    Federations federations = restClient.follow(services, LINK_FEDERATIONS, Federations.class);
    federation = federations.byName(name);
    if (federation == null) {
      federation = createItem(services, LINK_FEDERATIONS, createFederation(name));
    }
  }

  private Federation createFederation(String name) {
    Federation result = new Federation();
    result.setName(name);
    result.setSuperUserPassword(configuration.get(FEDERATION_SUPERUSER_PASSWORD));
    result.setBootstrap(configuration.get(FEDERATION_BOOTSTRAP));
    return result;
  }

  private void ensureDatabase() throws IOException {
    String name = configuration.get(DATABASE_NAME);
    Databases databases = restClient.follow(federation, LINK_DATABASES, Databases.class);
    Database database = databases.byName(name);
    if (database == null) {
      database = createItem(databases, createDatabase(name));
    }
    databaseUri = database.getSelfUri();
  }

  private Database createDatabase(String name) {
    Database result = new Database();
    result.setName(name);
    result.setAdminPassword(configured(DATABASE_ADMIN_PASSWORD));
    return result;
  }

  private void ensureFileSystemRoot() throws IOException {
    FileSystemRoots fileSystemRoots = restClient.follow(services, LINK_FILE_SYSTEM_ROOTS, FileSystemRoots.class);
    fileSystemRootUri = fileSystemRoots.first().getSelfUri();
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

  private Application createApplication(String applicationName) {
    Application result = new Application();
    result.setName(applicationName);
    return result;
  }

  private void ensureSpace() throws IOException {
    String spaceName = application.getName();
    Spaces spaces = restClient.follow(application, LINK_SPACES, Spaces.class);
    space = spaces.byName(spaceName);
    if (space == null) {
      space = createItem(spaces, LINK_ADD, createSpace(spaceName));
    }
  }

  private Space createSpace(String name) {
    Space result = new Space();
    result.setName(name);
    return result;
  }

  private void ensureSpaceRootLibrary() throws IOException {
    String name = configured(HOLDING_NAME);
    SpaceRootLibraries libraries = restClient.follow(space, LINK_SPACE_ROOT_LIBRARIES, SpaceRootLibraries.class);
    SpaceRootLibrary library = libraries.byName(name);
    if (library == null) {
      createItem(libraries, createSpaceRootLibrary(name));
    }
  }

  private SpaceRootLibrary createSpaceRootLibrary(String name) {
    SpaceRootLibrary result = new SpaceRootLibrary();
    result.setName(name);
    result.setXdbDatabase(databaseUri);
    return result;
  }

  private void ensureSpaceRootFolder() throws IOException {
    String name = space.getName();
    SpaceRootFolders spaceRootFolders = restClient.follow(space, LINK_SPACE_ROOT_FOLDERS, SpaceRootFolders.class);
    spaceRootFolder = spaceRootFolders.byName(name);
    if (spaceRootFolder == null) {
      spaceRootFolder = createItem(spaceRootFolders, createSpaceRootFolder(name));
    }
  }

  private SpaceRootFolder createSpaceRootFolder(String name) {
    SpaceRootFolder result = new SpaceRootFolder();
    result.setName(name);
    result.setFileSystemRoot(fileSystemRootUri);
    return result;
  }

  private void ensureFileSystemFolder() throws IOException {
    String name = configured(HOLDING_NAME);
    FileSystemFolders fileSystemFolders = restClient.follow(spaceRootFolder, LINK_FILE_SYSTEM_FOLDERS,
        FileSystemFolders.class);
    FileSystemFolder fileSystemFolder = fileSystemFolders.byName(name);
    if (fileSystemFolder == null) {
      fileSystemFolder = createItem(fileSystemFolders, createFileSystemFolder(name));
    }
    fileSystemFolderUri = fileSystemFolder.getSelfUri();
  }

  private FileSystemFolder createFileSystemFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(spaceRootFolder.getSelfUri());
    result.setSubPath("stores/" + STORE_NAME);
    return result;
  }

  private void ensureStore() throws IOException {
    String name = STORE_NAME;
    Stores stores = restClient.follow(application, LINK_STORES, Stores.class);
    Store store = stores.byName(name);
    if (store == null) {
      store = createItem(stores, createStore(name));
    }
    storeUri = store.getSelfUri();
  }

  private Store createStore(String name) {
    Store result = new Store();
    result.setName(name);
    result.setFileSystemFolder(fileSystemFolderUri);
    return result;
  }

  private void ensureReceptionFolder() throws IOException {
    String name = "reception-folder";
    FileSystemFolders receptionFolders = restClient.follow(spaceRootFolder, LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder receptionFolder = receptionFolders.byName(name);
    if (receptionFolder == null) {
      receptionFolder = createItem(receptionFolders, createReceptionFolder(name));
    }
    receptionFolderUri = receptionFolder.getSelfUri();
  }

  private FileSystemFolder createReceptionFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(spaceRootFolder.getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + RECEIVER_NODE_NAME);
    return result;
  }

  private void ensureIngestionFolder() throws IOException {
    String name = "ingestion-folder";
    FileSystemFolders fileSystemFolders = restClient.follow(spaceRootFolder, LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder fileSystemFolder = fileSystemFolders.byName(name);
    if (fileSystemFolder == null) {
      createItem(fileSystemFolders, createIngestionFolder(name));
    }
  }

  private FileSystemFolder createIngestionFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(spaceRootFolder.getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + "ingestion_node_01");
    return result;
  }

  private void ensureReceiverNode() throws IOException {
    String name = RECEIVER_NODE_NAME;
    ReceiverNodes receiverNodes = restClient.follow(application, LINK_RECEIVER_NODES, ReceiverNodes.class);
    ReceiverNode receiverNode = receiverNodes.byName(name);
    if (receiverNode == null) {
      createItem(receiverNodes, createReceiverNode(name));
    }
  }

  private ReceiverNode createReceiverNode(String name) {
    ReceiverNode result = new ReceiverNode();
    result.setName(name);
    result.setWorkingDirectory(receptionFolderUri);
    result.setLogsStore(storeUri);
    result.getSips().add(new Sip());
    return result;
  }

  private void ensureHolding() throws IOException {
    String name = configuration.get(HOLDING_NAME);
    Holdings holdings = restClient.follow(application, LINK_HOLDINGS, Holdings.class);
    Holding holding = holdings.byName(name);
    if (holding == null) {
      createItem(holdings, createHolding(name));
    }
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
    return restClient.post(uri, entity, type);
  }

}
