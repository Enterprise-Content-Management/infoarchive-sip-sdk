/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Applications;
import com.emc.ia.sdk.sip.ingestion.dto.Contents;
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
import com.emc.ia.sdk.sip.ingestion.dto.JobDefinition;
import com.emc.ia.sdk.sip.ingestion.dto.JobDefinitions;
import com.emc.ia.sdk.sip.ingestion.dto.JobInstance;
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
import com.emc.ia.sdk.support.NewInstance;
import com.emc.ia.sdk.support.http.BinaryPart;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.MediaTypes;
import com.emc.ia.sdk.support.http.TextPart;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.RestClient;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient, InfoArchiveLinkRelations, InfoArchiveConfiguration {

  private static final String FORMAT_XML = "xml";
  private static final String FORMAT_XSD = "xsd";
  private static final String WORKING_FOLDER_NAME = "working/";
  private static final String RECEIVER_NODE_NAME = "receiver_node_01";
  private static final String INGEST_NAME = "ingest";
  private static final String INGEST_NODE_NAME = "ingest_node_01";
  private static final String STORE_NAME = "filestore_01";

  private final RestCache configurationState = new RestCache();
  private Map<String, String> configuration;
  private RestClient restClient;
  private String aipsUri;

  public InfoArchiveRestClient() {
  }

  public InfoArchiveRestClient(RestClient restClient) {
    this.restClient = restClient;
  }

  protected RestCache getConfigurationState() {
    return configurationState;
  }

  @Override
  public void configure(Map<String, String> config) {
    setConfiguration(config);
    try {
      initRestClient();
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

  protected Map<String, String> setConfiguration(Map<String, String> config) {
    return configuration = config;
  }

  private void initRestClient() throws IOException {
    if (restClient == null) {
      HttpClient httpClient = NewInstance
          .fromConfiguration(configuration, HTTP_CLIENT_CLASSNAME, ApacheHttpClient.class.getName())
          .as(HttpClient.class);
      restClient = new RestClient(httpClient);
    }
    restClient.init(configuration.get(SERVER_AUTENTICATON_TOKEN));
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

  private void ensureFederation() throws IOException {
    String name = configuration.get(FEDERATION_NAME);
    Federations federations = restClient.follow(configurationState.getServices(), LINK_FEDERATIONS, Federations.class);
    configurationState.setFederation(federations.byName(name));
    if (configurationState.getFederation() == null) {
      createItem(federations, createFederation(name));
      configurationState.setFederation(restClient.refresh(federations).byName(name));
      Objects.requireNonNull(configurationState.getFederation(), "Could not create federation");
    }
  }

  private Federation createFederation(String name) {
    Federation result = new Federation();
    result.setName(name);
    result.setSuperUserPassword(configuration.get(FEDERATION_SUPERUSER_PASSWORD));
    result.setBootstrap(configuration.get(FEDERATION_BOOTSTRAP));
    return result;
  }

  private <T> T createItem(LinkContainer collection, T item) throws IOException {
    return createItem(collection, LINK_ADD, item);
  }

  public <T> T createItem(LinkContainer collection, String addLinkRelation, T item) throws IOException {
    try {
      return restClient.createCollectionItem(collection, addLinkRelation, item);
    } catch (IOException e) {
      if (e.getMessage().contains("DUPLICATE_KEY")) {
        // This can happen when multiple processes/threads attempt to create resources simultaneously
        return null;
      }
      throw e;
    }
  }

  private void ensureDatabase() throws IOException {
    String name = configuration.get(DATABASE_NAME);
    Databases databases = restClient.follow(configurationState.getFederation(), LINK_DATABASES, Databases.class);
    Database database = databases.byName(name);
    if (database == null) {
      createItem(databases, createDatabase(name));
      database = restClient.refresh(databases).byName(name);
      Objects.requireNonNull(database, "Could not create database");
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

  protected void ensureApplication() throws IOException {
    String applicationName = configuration.get(APPLICATION_NAME);
    Applications applications = restClient.follow(configurationState.getTenant(), LINK_APPLICATIONS,
        Applications.class);
    configurationState.setApplication(applications.byName(applicationName));
    if (configurationState.getApplication() == null) {
      createItem(applications, createApplication(applicationName));
      configurationState.setApplication(restClient.refresh(applications).byName(applicationName));
      Objects.requireNonNull(configurationState.getApplication(), "Could not create application");
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
      createItem(spaces, LINK_ADD, createSpace(spaceName));
      configurationState.setSpace(restClient.refresh(spaces).byName(spaceName));
      Objects.requireNonNull(configurationState.getSpace(), "Could not create space");
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
      createItem(spaceRootLibraries, createSpaceRootLibrary(name));
      configurationState.setSpaceRootLibrary(restClient.refresh(spaceRootLibraries).byName(name));
      Objects.requireNonNull(configurationState.getSpaceRootLibrary(), "Could not create space root library");
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
      createItem(spaceRootFolders, createSpaceRootFolder(name));
      configurationState.setSpaceRootFolder(restClient.refresh(spaceRootFolders).byName(name));
      Objects.requireNonNull(configurationState.getSpaceRootFolder(), "Could not create space root folder");
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
      createItem(fileSystemFolders, createFileSystemFolder(name));
      fileSystemFolder = restClient.refresh(fileSystemFolders).byName(name);
      Objects.requireNonNull(fileSystemFolder, "Could not create file system folder");
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
      createItem(stores, createStore(name));
      store = restClient.refresh(stores).byName(name);
      Objects.requireNonNull(store, "Could not create store");
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
      createItem(receptionFolders, createReceptionFolder(name));
      receptionFolder = restClient.refresh(receptionFolders).byName(name);
      Objects.requireNonNull(receptionFolder, "Could not create reception folder");
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
      fileSystemFolder = restClient.refresh(fileSystemFolders).byName(name);
      Objects.requireNonNull(fileSystemFolder, "Could not create ingestion folder");
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
      receiverNode = restClient.refresh(receiverNodes).byName(name);
      Objects.requireNonNull(receiverNode, "Could not create receiver node");
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
    IngestNodes ingestNodes = restClient.follow(configurationState.getApplication(), LINK_INGEST_NODES,
        IngestNodes.class);
    IngestNode ingestNode = ingestNodes.byName(name);
    if (ingestNode == null) {
      createItem(ingestNodes, createIngestionNode(name));
      ingestNode = restClient.refresh(ingestNodes).byName(name);
      Objects.requireNonNull(ingestNode, "Could not create ingest node");
    }
    configurationState.setIngestNodeUri(ingestNode.getSelfUri());
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
      retentionPolicy = restClient.refresh(retentionPolicies).byName(name);
      Objects.requireNonNull(retentionPolicy, "Could not create retention policy");
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
      createItem(pdis, createPdi(name));
      pdi = restClient.refresh(pdis).byName(name);
      Objects.requireNonNull(pdi, "Could not create PDI");
    }
    ensureContents(pdi, PDI_XML, FORMAT_XML);
    configurationState.setPdiUri(pdi.getSelfUri());
  }

  private void ensureContents(LinkContainer state, String configurationName, String format) throws IOException {
    Contents contents = restClient.follow(state, LINK_CONTENTS, Contents.class);
    if (contents.hasContent()) {
      return;
    }
    String content = configured(configurationName);
    try (InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
      restClient.post(state.getUri(LINK_CONTENTS), null,
          new TextPart("content", MediaTypes.HAL, "{ \"format\": \"" + format + "\" }"),
          new BinaryPart("file", stream, configurationName));
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
      createItem(pdiSchemas, createPdiSchema(name));
      pdiSchema = restClient.refresh(pdiSchemas).byName(name);
      Objects.requireNonNull(pdiSchema, "Could not create PDI schema");
    }
    ensureContents(pdiSchema, PDI_SCHEMA, FORMAT_XSD);
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
      createItem(ingests, createIngest(name));
      ingest = restClient.refresh(ingests).byName(name);
      Objects.requireNonNull(ingest, "Could not create ingest");
    }
    configurationState.setIngestUri(ingest.getSelfUri());
    ensureContents(ingest, INGEST_XML, FORMAT_XML);
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
      createItem(libraries, createLibrary(name));
      library = restClient.refresh(libraries).byName(name);
      Objects.requireNonNull(library, "Could not create library");
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
      holding = restClient.refresh(holdings).byName(name);
      Objects.requireNonNull(holding, "Could not create holding");
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

  protected void cacheAipsUri() {
    aipsUri = configurationState.getApplication().getUri(LINK_AIPS);
  }

  @Override
  public String ingest(InputStream sip) throws IOException {
    Objects.requireNonNull(aipsUri, "Did you forget to call configure()?");
    ReceptionResponse response = restClient.post(aipsUri, ReceptionResponse.class,
        new TextPart("format", "sip_zip"), new BinaryPart("sip", sip, "IASIP.zip"));
    IngestionResponse ingestionResponse = restClient.put(response.getUri(LINK_INGEST), IngestionResponse.class);
    return ingestionResponse.getAipId();
  }

  @Override
  public String confirm(String aipId) throws IOException {
    Objects.requireNonNull(aipId, "Invalid aipId");
    JobDefinitions jobDefinitions = restClient.follow(configurationState.getServices(), LINK_JOB_DEFINITIONS, JobDefinitions.class);
    JobDefinition jobDefinition = restClient.get(jobDefinitions.getSelfUri() + LINK_JOB_CONFIRMATION, JobDefinition.class);
    JobInstance jobInstance = restClient.post(jobDefinition.getUri(LINK_JOB_INSTANCES), JobInstance.class, new TextPart("isNow", "true"));
    return restClient.get(jobInstance.getSelfUri(), JobInstance.class).getStatus();
  }
}
