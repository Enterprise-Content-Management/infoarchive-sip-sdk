/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
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
import com.emc.ia.sdk.sip.ingestion.dto.Ingest;
import com.emc.ia.sdk.sip.ingestion.dto.IngestConfig;
import com.emc.ia.sdk.sip.ingestion.dto.IngestNode;
import com.emc.ia.sdk.sip.ingestion.dto.IngestNodes;
import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Ingests;
import com.emc.ia.sdk.sip.ingestion.dto.Libraries;
import com.emc.ia.sdk.sip.ingestion.dto.Library;
import com.emc.ia.sdk.sip.ingestion.dto.Pdi;
import com.emc.ia.sdk.sip.ingestion.dto.PdiConfig;
import com.emc.ia.sdk.sip.ingestion.dto.PdiSchema;
import com.emc.ia.sdk.sip.ingestion.dto.PdiSchemas;
import com.emc.ia.sdk.sip.ingestion.dto.Pdis;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNode;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.RetentionClass;
import com.emc.ia.sdk.sip.ingestion.dto.RetentionPolicies;
import com.emc.ia.sdk.sip.ingestion.dto.RetentionPolicy;
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
import com.emc.ia.sdk.sip.ingestion.dto.SubPriority;
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
  private static final String INGEST_NODE_NAME = "ingestion_node_01";
  private static final String STORE_NAME = "filestore_01";
  private static final String INGEST_NAME = "ingest";

  private final RestClient restClient;
  private final RestCache configurationState = new RestCache();
  private Map<String, String> configuration;
  private String aipsUri;

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
      ensureIngestNode();
      ensureRetentionPolicy();
      ensurePdi();
      ensurePdiSchema();
      ensureIngest();
      ensureLibrary();
      ensureHolding();
      cacheAipsUri();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void configureRestClient() throws IOException {
    List<Header> headers = new ArrayList<Header>();
    headers.add(new BasicHeader("Authorization", "Bearer " + configuration.get(SERVER_AUTENTICATON_TOKEN)));
    headers.add(new BasicHeader("Accept", MediaTypes.HAL));
    restClient.setHeaders(headers);
    configurationState.setServices(restClient.get(configured(SERVER_URI), Services.class));
  }

  public String configured(String name) {
    String result = configuration.get(name);
    Objects.requireNonNull(result, "Missing " + name);
    return result;
  }

  private void ensureTenant() throws IOException {
    configurationState.setTenant(restClient.follow(configurationState.getServices(), LINK_TENANT, Tenant.class));
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
    Federations federations = restClient.follow(configurationState.getServices(), LINK_FEDERATIONS, Federations.class);
    configurationState.setFederation(federations.byName(name));
    if (configurationState.getFederation() == null) {
      configurationState.setFederation(
          createItem(configurationState.getServices(), LINK_FEDERATIONS, createFederation(name)));
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
    Databases databases = restClient.follow(configurationState.getFederation(), LINK_DATABASES, Databases.class);
    Database database = databases.byName(name);
    if (database == null) {
      database = createItem(databases, createDatabase(name));
    }
    configurationState.setDatabaseUri(database.getSelfUri());
  }

  private Database createDatabase(String name) {
    Database result = new Database();
    result.setName(name);
    result.setAdminPassword(configured(DATABASE_ADMIN_PASSWORD));
    return result;
  }

  private void ensureFileSystemRoot() throws IOException {
    FileSystemRoots fileSystemRoots = restClient.follow(configurationState.getServices(), LINK_FILE_SYSTEM_ROOTS,
        FileSystemRoots.class);
    configurationState.setFileSystemRootUri(fileSystemRoots.first().getSelfUri());
  }

  private void ensureApplication() throws IOException {
    String applicationName = configuration.get(APPLICATION_NAME);
    Applications applications = restClient.follow(configurationState.getTenant(), LINK_APPLICATIONS,
        Applications.class);
    configurationState.setApplication(applications.byName(applicationName));
    if (configurationState.getApplication() == null) {
      configurationState.setApplication(createItem(applications, createApplication(applicationName)));
      Objects.requireNonNull(configurationState.getApplication(), "Could not create application " + applicationName);
    }
  }

  private Application createApplication(String applicationName) {
    Application result = new Application();
    result.setName(applicationName);
    return result;
  }

  private void ensureSpace() throws IOException {
    String spaceName = configurationState.getApplication().getName();
    Spaces spaces = restClient.follow(configurationState.getApplication(), LINK_SPACES, Spaces.class);
    configurationState.setSpace(spaces.byName(spaceName));
    if (configurationState.getSpace() == null) {
      configurationState.setSpace(createItem(spaces, LINK_ADD, createSpace(spaceName)));
    }
  }

  private Space createSpace(String name) {
    Space result = new Space();
    result.setName(name);
    return result;
  }

  private void ensureSpaceRootLibrary() throws IOException {
    String name = configured(HOLDING_NAME);
    SpaceRootLibraries spaceRootLibraries = restClient.follow(configurationState.getSpace(), LINK_SPACE_ROOT_LIBRARIES,
        SpaceRootLibraries.class);
    configurationState.setSpaceRootLibrary(spaceRootLibraries.byName(name));
    if (configurationState.getSpaceRootLibrary() == null) {
      configurationState.setSpaceRootLibrary(createItem(spaceRootLibraries, createSpaceRootLibrary(name)));
    }
  }

  private SpaceRootLibrary createSpaceRootLibrary(String name) {
    SpaceRootLibrary result = new SpaceRootLibrary();
    result.setName(name);
    result.setXdbDatabase(configurationState.getDatabaseUri());
    return result;
  }

  private void ensureSpaceRootFolder() throws IOException {
    String name = configurationState.getSpace().getName();
    SpaceRootFolders spaceRootFolders = restClient.follow(configurationState.getSpace(), LINK_SPACE_ROOT_FOLDERS,
        SpaceRootFolders.class);
    configurationState.setSpaceRootFolder(spaceRootFolders.byName(name));
    if (configurationState.getSpaceRootFolder() == null) {
      configurationState.setSpaceRootFolder(createItem(spaceRootFolders, createSpaceRootFolder(name)));
    }
  }

  private SpaceRootFolder createSpaceRootFolder(String name) {
    SpaceRootFolder result = new SpaceRootFolder();
    result.setName(name);
    result.setFileSystemRoot(configurationState.getFileSystemRootUri());
    return result;
  }

  private void ensureFileSystemFolder() throws IOException {
    String name = configured(HOLDING_NAME);
    FileSystemFolders fileSystemFolders = restClient.follow(configurationState.getSpaceRootFolder(),
        LINK_FILE_SYSTEM_FOLDERS,
        FileSystemFolders.class);
    FileSystemFolder fileSystemFolder = fileSystemFolders.byName(name);
    if (fileSystemFolder == null) {
      fileSystemFolder = createItem(fileSystemFolders, createFileSystemFolder(name));
    }
    configurationState.setFileSystemFolderUri(fileSystemFolder.getSelfUri());
  }

  private FileSystemFolder createFileSystemFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(configurationState.getSpaceRootFolder().getSelfUri());
    result.setSubPath("stores/" + STORE_NAME);
    return result;
  }

  private void ensureStore() throws IOException {
    String name = STORE_NAME;
    Stores stores = restClient.follow(configurationState.getApplication(), LINK_STORES, Stores.class);
    Store store = stores.byName(name);
    if (store == null) {
      store = createItem(stores, createStore(name));
    }
    configurationState.setStoreUri(store.getSelfUri());
  }

  private Store createStore(String name) {
    Store result = new Store();
    result.setName(name);
    result.setFileSystemFolder(configurationState.getFileSystemFolderUri());
    return result;
  }

  private void ensureReceptionFolder() throws IOException {
    String name = "reception-folder";
    FileSystemFolders receptionFolders = restClient.follow(configurationState.getSpaceRootFolder(),
        LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder receptionFolder = receptionFolders.byName(name);
    if (receptionFolder == null) {
      receptionFolder = createItem(receptionFolders, createReceptionFolder(name));
    }
    configurationState.setReceptionFolderUri(receptionFolder.getSelfUri());
  }

  private FileSystemFolder createReceptionFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(configurationState.getSpaceRootFolder().getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + RECEIVER_NODE_NAME);
    return result;
  }

  private void ensureIngestionFolder() throws IOException {
    String name = "ingestion-folder";
    FileSystemFolders fileSystemFolders = restClient.follow(configurationState.getSpaceRootFolder(),
        LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder fileSystemFolder = fileSystemFolders.byName(name);
    if (fileSystemFolder == null) {
      createItem(fileSystemFolders, createIngestionFolder(name));
    }
  }

  private FileSystemFolder createIngestionFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(configurationState.getSpaceRootFolder().getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + INGEST_NODE_NAME);
    return result;
  }

  private void ensureReceiverNode() throws IOException {
    String name = RECEIVER_NODE_NAME;
    ReceiverNodes receiverNodes = restClient.follow(configurationState.getApplication(), LINK_RECEIVER_NODES,
        ReceiverNodes.class);
    ReceiverNode receiverNode = receiverNodes.byName(name);
    if (receiverNode == null) {
      createItem(receiverNodes, createReceiverNode(name));
    }
  }

  private ReceiverNode createReceiverNode(String name) {
    ReceiverNode result = new ReceiverNode();
    result.setName(name);
    result.setWorkingDirectory(configurationState.getReceptionFolderUri());
    result.setLogsStore(configurationState.getStoreUri());
    result.getSips().add(new Sip());
    return result;
  }

  private void ensureIngestNode() throws IOException {
    String name = INGEST_NODE_NAME;
    IngestNodes ingestionNodes = restClient.follow(configurationState.getApplication(), LINK_INGEST_NODES,
        IngestNodes.class);
    IngestNode ingestionNode = ingestionNodes.byName(name);
    if (ingestionNode == null) {
      ingestionNode = createItem(ingestionNodes, createIngestionNode(name));
    }
    configurationState.setIngestNodeUri(ingestionNode.getSelfUri());
  }

  private IngestNode createIngestionNode(String name) {
    IngestNode result = new IngestNode();
    result.setName(name);
    result.setWorkingDirectory(configurationState.getFileSystemFolderUri());
    return result;
  }

  private void ensureRetentionPolicy() throws IOException {
    String name = configured(RETENTION_POLICY_NAME);
    RetentionPolicies retentionPolicies = restClient.follow(configurationState.getTenant(), LINK_RETENTION_POLICIES,
        RetentionPolicies.class);
    RetentionPolicy retentionPolicy = retentionPolicies.byName(name);
    if (retentionPolicy == null) {
      createItem(retentionPolicies, LINK_SELF, createRetentionPolicy(name));
    }
  }

  private RetentionPolicy createRetentionPolicy(String name) {
    RetentionPolicy result = new RetentionPolicy();
    result.setName(name);
    return result;
  }

  private void ensurePdi() throws IOException {
    String name = configured(RETENTION_POLICY_NAME);
    Pdis pdis = restClient.follow(configurationState.getApplication(), LINK_PDIS, Pdis.class);
    Pdi pdi = pdis.byName(name);
    if (pdi == null) {
      pdi = createItem(pdis, createPdi(name));
    }
    uploadContents(pdi, PDI_XML);
    configurationState.setPdiUri(pdi.getSelfUri());
  }

  private void uploadContents(LinkContainer state, String configurationName) throws IOException {
    String contents = configured(configurationName);
    try (InputStream stream = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8))) {
      HttpEntity entity = MultipartEntityBuilder.create()
          .addPart("content", new StringBody("{ \"format\": \"application/xml\" }", ContentType.create(MediaTypes.HAL)))
          .addPart("file", new InputStreamBody(stream, ContentType.APPLICATION_OCTET_STREAM, configurationName))
          .build();
      restClient.post(state.getUri(LINK_CONTENTS), entity, null);
    }
  }

  private Pdi createPdi(String name) {
    Pdi result = new Pdi();
    result.setName(name);
    return result;
  }

  private void ensurePdiSchema() throws IOException {
    String name = configured(PDI_SCHEMA_NAME);
    PdiSchemas pdiSchemas = restClient.follow(configurationState.getApplication(), LINK_PDI_SCHEMAS, PdiSchemas.class);
    PdiSchema pdiSchema = pdiSchemas.byName(name);
    if (pdiSchema == null) {
      pdiSchema = createItem(pdiSchemas, createPdiSchema(name));
    }
    uploadContents(pdiSchema, PDI_SCHEMA);
  }

  private PdiSchema createPdiSchema(String name) {
    PdiSchema result = new PdiSchema();
    result.setName(name);
    return result;
  }

  private void ensureIngest() throws IOException {
    String name = INGEST_NAME;
    Ingests ingests = restClient.follow(configurationState.getApplication(), LINK_INGESTS, Ingests.class);
    Ingest ingest = ingests.byName(name);
    if (ingest == null) {
      ingest = createItem(ingests, createIngest(name));
    }
    configurationState.setIngestUri(ingest.getSelfUri());
  }

  private Ingest createIngest(String name) {
    Ingest result = new Ingest();
    result.setName(name);
    return result;
  }

  private void ensureLibrary() throws IOException {
    String name = configured(HOLDING_NAME);
    Libraries libraries = restClient.follow(configurationState.getSpaceRootLibrary(), LINK_LIBRARIES, Libraries.class);
    Library library = libraries.byName(name);
    if (library == null) {
      library = createItem(libraries, createLibrary(name));
    }
    configurationState.setLibraryUri(library.getSelfUri());
  }

  private Library createLibrary(String name) {
    Library result = new Library();
    result.setName(name);
    result.setSubPath("aips/" + configured(APPLICATION_NAME).replace(' ', '-'));
    return result;
  }

  private void ensureHolding() throws IOException {
    String name = configuration.get(HOLDING_NAME);
    Holdings holdings = restClient.follow(configurationState.getApplication(), LINK_HOLDINGS, Holdings.class);
    Holding holding = holdings.byName(name);
    if (holding == null) {
      createItem(holdings, createHolding(name));
    }
  }

  private Holding createHolding(String holdingName) {
    Holding result = new Holding();
    result.setName(holdingName);
    result.setAllStores(configurationState.getStoreUri());
    IngestConfig ingestConfig = new IngestConfig();
    ingestConfig.setIngest(configurationState.getIngestUri());
    result.getIngestConfigs().add(ingestConfig);
    result.getIngestNodes().add(configurationState.getIngestNodeUri());
    SubPriority priority = new SubPriority();
    priority.setPriority(0);
    priority.setDeadline(100);
    result.getSubPriorities().add(priority);
    priority = new SubPriority();
    priority.setPriority(1);
    priority.setDeadline(200);
    result.getSubPriorities().add(priority);
    result.setXdbLibraryParent(configurationState.getLibraryUri());
    RetentionClass retentionClass = new RetentionClass();
    retentionClass.getPolicies().add(configured(RETENTION_POLICY_NAME));
    result.getRetentionClasses().add(retentionClass);
    PdiConfig pdiConfig = new PdiConfig();
    pdiConfig.setPdi(configurationState.getPdiUri());
    pdiConfig.setSchema(configured(PDI_SCHEMA_NAME));
    result.getPdiConfigs().add(pdiConfig);
    return result;
  }

  private void cacheAipsUri() {
    aipsUri = configurationState.getApplication().getUri(LINK_AIPS);
  }

  @Override
  public String ingest(InputStream sip) throws IOException {
    Objects.requireNonNull(aipsUri, "Did you forget to call configure()?");
    ReceptionResponse response = upload(aipsUri, sip, ReceptionResponse.class);
    Link ingestLink = response.getLinks().get(LINK_INGEST);
    IngestionResponse ingestionResponse = restClient.put(ingestLink.getHref(), IngestionResponse.class);
    return ingestionResponse.getAipId();
  }

  private <T> T upload(String uri, InputStream sip, Class<T> type) throws IOException {
    // TODO - what should be the file name here ? IASIP.zip is Ok ?
    InputStreamBody file = new InputStreamBody(sip, ContentType.APPLICATION_OCTET_STREAM, "IASIP.zip");
    HttpEntity entity = MultipartEntityBuilder.create()
        .addTextBody("format", "sip_zip")
        .addPart("sip", file)
        .build();
    return restClient.post(uri, entity, type);
  }

}
