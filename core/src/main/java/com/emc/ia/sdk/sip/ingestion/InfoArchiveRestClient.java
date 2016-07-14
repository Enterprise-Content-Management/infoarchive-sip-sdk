/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.emc.ia.sdk.sip.ingestion.dto.Aic;
import com.emc.ia.sdk.sip.ingestion.dto.Aics;
import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Applications;
import com.emc.ia.sdk.sip.ingestion.dto.Contents;
import com.emc.ia.sdk.sip.ingestion.dto.Criterion;
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
import com.emc.ia.sdk.sip.ingestion.dto.Namespace;
import com.emc.ia.sdk.sip.ingestion.dto.Operand;
import com.emc.ia.sdk.sip.ingestion.dto.Pdi;
import com.emc.ia.sdk.sip.ingestion.dto.PdiConfig;
import com.emc.ia.sdk.sip.ingestion.dto.PdiSchema;
import com.emc.ia.sdk.sip.ingestion.dto.PdiSchemas;
import com.emc.ia.sdk.sip.ingestion.dto.Pdis;
import com.emc.ia.sdk.sip.ingestion.dto.Queries;
import com.emc.ia.sdk.sip.ingestion.dto.Query;
import com.emc.ia.sdk.sip.ingestion.dto.Quota;
import com.emc.ia.sdk.sip.ingestion.dto.Quotas;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNode;
import com.emc.ia.sdk.sip.ingestion.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.ResultConfigurationHelper;
import com.emc.ia.sdk.sip.ingestion.dto.ResultConfigurationHelpers;
import com.emc.ia.sdk.sip.ingestion.dto.RetentionClass;
import com.emc.ia.sdk.sip.ingestion.dto.RetentionPolicies;
import com.emc.ia.sdk.sip.ingestion.dto.RetentionPolicy;
import com.emc.ia.sdk.sip.ingestion.dto.Search;
import com.emc.ia.sdk.sip.ingestion.dto.SearchComposition;
import com.emc.ia.sdk.sip.ingestion.dto.SearchCompositions;
import com.emc.ia.sdk.sip.ingestion.dto.Searches;
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
import com.emc.ia.sdk.sip.ingestion.dto.XForm;
import com.emc.ia.sdk.sip.ingestion.dto.XForms;
import com.emc.ia.sdk.sip.ingestion.dto.XdbPdiConfig;
import com.emc.ia.sdk.sip.ingestion.dto.query.QueryFormatter;
import com.emc.ia.sdk.sip.ingestion.dto.query.QueryResult;
import com.emc.ia.sdk.sip.ingestion.dto.query.SearchQuery;
import com.emc.ia.sdk.support.NewInstance;
import com.emc.ia.sdk.support.RepeatingConfigReader;
import com.emc.ia.sdk.support.http.BinaryPart;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.MediaTypes;
import com.emc.ia.sdk.support.http.ResponseFactory;
import com.emc.ia.sdk.support.http.TextPart;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.RestClient;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient, InfoArchiveLinkRelations, InfoArchiveConfiguration {

  private static final int WAIT_RACE_CONDITION_MS = 2000;
  private static final String FORMAT_XML = "xml";
  private static final String FORMAT_XSD = "xsd";
  private static final String WORKING_FOLDER_NAME = "working/";
  private static final String RECEIVER_NODE_NAME = "receiver_node_01";
  private static final String INGEST_NAME = "ingest";
  private static final String INGEST_NODE_NAME = "ingest_node_01";
  private static final String STORE_NAME = "filestore_01";
  private static final String DEFAULT_RESULT_HELPER_NAME = "result_helper";

  private final RestCache configurationState = new RestCache();
  private final ResponseFactory<QueryResult> queryResultFactory = new QueryResultFactory();
  private final QueryFormatter queryFormatter = new QueryFormatter();

  private Map<String, String> configuration;
  private RestClient restClient;
  private String aipsUri;
  private Map<String, String> dipUrisByAicName;

  public InfoArchiveRestClient() {
    this(null);
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
      ensureAic();
      ensureQuota();
      ensureQuery();
      ensureResultConfigurationHelper();
      ensureSearch();
      ensureSearchComposition();
      cacheAipsUri();
      cacheDipUris();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  protected Map<String, String> setConfiguration(Map<String, String> config) {
    return configuration = config;
  }

  private void initRestClient() throws IOException {
    if (restClient == null) {
      HttpClient httpClient =
          NewInstance.fromConfiguration(configuration, HTTP_CLIENT_CLASSNAME, ApacheHttpClient.class.getName())
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
      createItem(configurationState.getServices(), LINK_FEDERATIONS, createFederation(name));
      configurationState.setFederation(restClient.refresh(federations)
        .byName(name));
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

  public <T> T createItem(LinkContainer collection, String linkRelation, T item) throws IOException {
    try {
      return restClient.createCollectionItem(collection, linkRelation, item);
    } catch (IOException e) {
      handleDuplicateObjects(e);
      return null;
    }
  }

  private void handleDuplicateObjects(IOException exception) throws IOException {
    if (!isDuplicateObjectException(exception.getMessage())) {
      throw exception;
    }
    // This can happen when multiple processes/threads attempt to create resources simultaneously
    waitForOtherInstanceToCreateObjects();
  }

  private boolean isDuplicateObjectException(String error) {
    return error.contains("DUPLICATE_KEY") || error.contains("NAME_ALREADY_EXISTS");
  }

  private void waitForOtherInstanceToCreateObjects() {
    try {
      Thread.sleep(WAIT_RACE_CONDITION_MS);
    } catch (InterruptedException e1) {
      // Ignore
    }
  }

  private void ensureDatabase() throws IOException {
    String name = configuration.get(DATABASE_NAME);
    Databases databases = restClient.follow(configurationState.getFederation(), LINK_DATABASES, Databases.class);
    Database database = databases.byName(name);
    if (database == null) {
      createItem(databases, createDatabase(name));
      database = restClient.refresh(databases)
        .byName(name);
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
    FileSystemRoots fileSystemRoots =
        restClient.follow(configurationState.getServices(), LINK_FILE_SYSTEM_ROOTS, FileSystemRoots.class);
    configurationState.setFileSystemRootUri(fileSystemRoots.first()
      .getSelfUri());
  }

  protected void ensureApplication() throws IOException {
    String applicationName = configuration.get(APPLICATION_NAME);
    Applications applications =
        restClient.follow(configurationState.getTenant(), LINK_APPLICATIONS, Applications.class);
    configurationState.setApplication(applications.byName(applicationName));
    if (configurationState.getApplication() == null) {
      createItem(applications, createApplication(applicationName));
      configurationState.setApplication(restClient.refresh(applications)
        .byName(applicationName));
      Objects.requireNonNull(configurationState.getApplication(), "Could not create application");
    }
  }

  private Application createApplication(String applicationName) {
    Application result = new Application();
    result.setName(applicationName);
    return result;
  }

  private void ensureSpace() throws IOException {
    String spaceName = configurationState.getApplication()
      .getName();
    Spaces spaces = restClient.follow(configurationState.getApplication(), LINK_SPACES, Spaces.class);
    configurationState.setSpace(spaces.byName(spaceName));
    if (configurationState.getSpace() == null) {
      createItem(spaces, LINK_ADD, createSpace(spaceName));
      configurationState.setSpace(restClient.refresh(spaces)
        .byName(spaceName));
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
    SpaceRootLibraries spaceRootLibraries =
        restClient.follow(configurationState.getSpace(), LINK_SPACE_ROOT_LIBRARIES, SpaceRootLibraries.class);
    configurationState.setSpaceRootLibrary(spaceRootLibraries.byName(name));
    if (configurationState.getSpaceRootLibrary() == null) {
      createItem(spaceRootLibraries, createSpaceRootLibrary(name));
      configurationState.setSpaceRootLibrary(restClient.refresh(spaceRootLibraries)
        .byName(name));
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
    String name = configurationState.getSpace()
      .getName();
    SpaceRootFolders spaceRootFolders =
        restClient.follow(configurationState.getSpace(), LINK_SPACE_ROOT_FOLDERS, SpaceRootFolders.class);
    configurationState.setSpaceRootFolder(spaceRootFolders.byName(name));
    if (configurationState.getSpaceRootFolder() == null) {
      createItem(spaceRootFolders, createSpaceRootFolder(name));
      configurationState.setSpaceRootFolder(restClient.refresh(spaceRootFolders)
        .byName(name));
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
    FileSystemFolders fileSystemFolders =
        restClient.follow(configurationState.getSpaceRootFolder(), LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder fileSystemFolder = fileSystemFolders.byName(name);
    if (fileSystemFolder == null) {
      createItem(fileSystemFolders, createFileSystemFolder(name));
      fileSystemFolder = restClient.refresh(fileSystemFolders)
        .byName(name);
      Objects.requireNonNull(fileSystemFolder, "Could not create file system folder");
    }
    configurationState.setFileSystemFolderUri(fileSystemFolder.getSelfUri());
  }

  private FileSystemFolder createFileSystemFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(configurationState.getSpaceRootFolder()
      .getSelfUri());
    result.setSubPath("stores/" + STORE_NAME);
    return result;
  }

  private void ensureStore() throws IOException {
    String name = STORE_NAME;
    Stores stores = restClient.follow(configurationState.getApplication(), LINK_STORES, Stores.class);
    Store store = stores.byName(name);
    if (store == null) {
      createItem(stores, createStore(name));
      store = restClient.refresh(stores)
        .byName(name);
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
    FileSystemFolders receptionFolders =
        restClient.follow(configurationState.getSpaceRootFolder(), LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder receptionFolder = receptionFolders.byName(name);
    if (receptionFolder == null) {
      createItem(receptionFolders, createReceptionFolder(name));
      receptionFolder = restClient.refresh(receptionFolders)
        .byName(name);
      Objects.requireNonNull(receptionFolder, "Could not create reception folder");
    }
    configurationState.setReceptionFolderUri(receptionFolder.getSelfUri());
  }

  private FileSystemFolder createReceptionFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(configurationState.getSpaceRootFolder()
      .getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + RECEIVER_NODE_NAME);
    return result;
  }

  private void ensureIngestionFolder() throws IOException {
    String name = "ingestion-folder";
    FileSystemFolders fileSystemFolders =
        restClient.follow(configurationState.getSpaceRootFolder(), LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder fileSystemFolder = fileSystemFolders.byName(name);
    if (fileSystemFolder == null) {
      createItem(fileSystemFolders, createIngestionFolder(name));
      fileSystemFolder = restClient.refresh(fileSystemFolders)
        .byName(name);
      Objects.requireNonNull(fileSystemFolder, "Could not create ingestion folder");
    }
  }

  private FileSystemFolder createIngestionFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(configurationState.getSpaceRootFolder()
      .getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + INGEST_NODE_NAME);
    return result;
  }

  private void ensureReceiverNode() throws IOException {
    String name = RECEIVER_NODE_NAME;
    ReceiverNodes receiverNodes =
        restClient.follow(configurationState.getApplication(), LINK_RECEIVER_NODES, ReceiverNodes.class);
    ReceiverNode receiverNode = receiverNodes.byName(name);
    if (receiverNode == null) {
      createItem(receiverNodes, createReceiverNode(name));
      receiverNode = restClient.refresh(receiverNodes)
        .byName(name);
      Objects.requireNonNull(receiverNode, "Could not create receiver node");
    }
  }

  private ReceiverNode createReceiverNode(String name) {
    ReceiverNode result = new ReceiverNode();
    result.setName(name);
    result.setWorkingDirectory(configurationState.getReceptionFolderUri());
    result.setLogsStore(configurationState.getStoreUri());
    result.getSips()
      .add(new Sip());
    return result;
  }

  private void ensureIngestNode() throws IOException {
    String name = INGEST_NODE_NAME;
    IngestNodes ingestNodes =
        restClient.follow(configurationState.getApplication(), LINK_INGEST_NODES, IngestNodes.class);
    IngestNode ingestNode = ingestNodes.byName(name);
    if (ingestNode == null) {
      createItem(ingestNodes, createIngestionNode(name));
      ingestNode = restClient.refresh(ingestNodes)
        .byName(name);
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
    RetentionPolicies retentionPolicies =
        restClient.follow(configurationState.getTenant(), LINK_RETENTION_POLICIES, RetentionPolicies.class);
    RetentionPolicy retentionPolicy = retentionPolicies.byName(name);
    if (retentionPolicy == null) {
      createItem(retentionPolicies, LINK_SELF, createRetentionPolicy(name));
      retentionPolicy = restClient.refresh(retentionPolicies)
        .byName(name);
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
      pdi = restClient.refresh(pdis)
        .byName(name);
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
      try {
        restClient.post(state.getUri(LINK_CONTENTS), null,
            new TextPart("content", MediaTypes.HAL, "{ \"format\": \"" + format + "\" }"),
            new BinaryPart("file", stream, configurationName));
      } catch (IOException e) {
        handleDuplicateObjects(e);
      }
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
      pdiSchema = restClient.refresh(pdiSchemas)
        .byName(name);
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
      ingest = restClient.refresh(ingests)
        .byName(name);
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
      library = restClient.refresh(libraries)
        .byName(name);
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
      holding = restClient.refresh(holdings)
        .byName(name);
      Objects.requireNonNull(holding, "Could not create holding");
    }
  }

  private void ensureAic() throws IOException {
    String name = configuration.get(AIC_NAME);
    Aics aics = restClient.follow(configurationState.getApplication(), LINK_AICS, Aics.class);
    Aic aic = aics.byName(name);
    if (aic == null) {
      createItem(aics, createAic(name));
      aic = restClient.refresh(aics)
        .byName(name);
      Objects.requireNonNull(aic, "Could not create aic");
    }
    configurationState.setAicUri(aic.getSelfUri());

  }

  private Aic createAic(String name) {
    Aic aic = new Aic();
    aic.setName(name);
    aic.setCriterias(createCriteria());
    return aic;
  }

  private List<Criterion> createCriteria() {
    List<Criterion> criteria = new ArrayList<>();

    RepeatingConfigReader reader = new RepeatingConfigReader("criteria", Arrays.asList(CRITERIA_NAME, CRITERIA_LABEL,
        CRITERIA_TYPE, CRITERIA_PKEYMINATTR, CRITERIA_PKEYMAXATTR, CRITERIA_PKEYVALUESATTR, CRITERIA_INDEXED));

    List<Map<String, String>> criterionConfigurations = reader.read(configuration);
    for (Map<String, String> cfg : criterionConfigurations) {
      Criterion criterion = new Criterion();
      criterion.setIndexed(Boolean.parseBoolean(cfg.get(CRITERIA_INDEXED)));
      criterion.setLabel(cfg.get(CRITERIA_LABEL));
      criterion.setName(cfg.get(CRITERIA_NAME));
      criterion.setpKeyMaxAttr(cfg.get(CRITERIA_PKEYMINATTR));
      criterion.setpKeyMinAttr(cfg.get(CRITERIA_PKEYMAXATTR));
      criterion.setpKeyValuesAttr(cfg.get(CRITERIA_PKEYVALUESATTR));
      criterion.setType(cfg.get(CRITERIA_TYPE));
      criteria.add(criterion);
    }

    return criteria;
  }

  private void ensureQuery() throws IOException {
    String rawQueryNames = configuration.get(QUERY_NAME);
    List<String> queryUris =  new ArrayList<String>();
    if (rawQueryNames != null && !rawQueryNames.isEmpty()) {
      String[] queryNames = rawQueryNames.split(",");
      for (String queryName : queryNames) {
        Queries queries = restClient.follow(configurationState.getApplication(), LINK_QUERIES, Queries.class);
        Query query = queries.byName(queryName);
        if (query == null) {
          createItem(queries, createQuery(queryName));
          query = restClient.refresh(queries)
            .byName(queryName);
          Objects.requireNonNull(query, "Could not create query");
        }
        queryUris.add(query.getSelfUri());
      }
      configurationState.setQueryUris(queryUris);
    }
  }

  private void ensureQuota() throws IOException {
    String name = configuration.get(QUOTA_NAME);

    Quotas quotas = restClient.follow(configurationState.getApplication(), LINK_QUERY_QUOTAS, Quotas.class);
    Quota quota = quotas.byName(name);
    if (quota == null) {
      createItem(quotas, createQuota(name));
      quota = restClient.refresh(quotas)
        .byName(name);
      Objects.requireNonNull(quota, "Could not create query");
    }
    configurationState.setQuotaUri(quota.getSelfUri());

  }

  private Quota createQuota(String name) {
    Quota quota = new Quota();
    quota.setName(name);

    int aipQuota = getOptionalInt(QUOTA_AIP, 0);
    quota.setAipQuota(aipQuota);

    int aiuQuota = getOptionalInt(QUOTA_AIU, 0);
    quota.setAiuQuota(aiuQuota);

    int dipQuota = getOptionalInt(QUOTA_DIP, 0);
    quota.setDipQuota(dipQuota);

    return quota;
  }

  private int getOptionalInt(String name, int defaultValue) {
    String value = configuration.get(name);
    if (value == null || value.isEmpty()) {
      return defaultValue;
    } else {
      return Integer.parseInt(value);
    }
  }

  private Query createQuery(String name) {
    Query query = new Query();
    query.setName(name);

    query.setResultRootElement(configuration.get(resolveTemplatedKey(QUERY_RESULT_ROOT_ELEMENT_TEMPLATE, name)));
    query.setResultRootNsEnabled(
        Boolean.parseBoolean(configuration.get(resolveTemplatedKey(QUERY_RESULT_ROOT_NS_ENABLED_TEMPLATE, name))));
    query.setResultSchema(configuration.get(resolveTemplatedKey(QUERY_RESULT_SCHEMA_TEMPLATE, name)));

    query.setNamespaces(createNamespaces(name));
    query.setXdbPdiConfigs(createXdbPdiConfigs(name));

    query.setQuota(configurationState.getQuotaUri());
    query.setQuotaAsync(configurationState.getQuotaUri());
    query.getAics()
        .add(configurationState.getAicUri());

    return query;

  }

  private List<Namespace> createNamespaces(String name) {
    RepeatingConfigReader reader = new RepeatingConfigReader("namespace",
        resolveTemplatedKeys(Arrays.asList(QUERY_NAMESPACE_PREFIX_TEMPLATE, QUERY_NAMESPACE_URI_TEMPLATE), name));
    List<Map<String, String>> namespaceCfgs = reader.read(configuration);
    List<Namespace> namespaces = new ArrayList<>();
    for (Map<String, String> cfg : namespaceCfgs) {
      Namespace namespace = new Namespace();
      namespace.setPrefix(cfg.get(resolveTemplatedKey(QUERY_NAMESPACE_PREFIX_TEMPLATE, name)));
      namespace.setUri(cfg.get(resolveTemplatedKey(QUERY_NAMESPACE_URI_TEMPLATE, name)));

      namespaces.add(namespace);
    }
    return namespaces;
  }

  private List<XdbPdiConfig> createXdbPdiConfigs(String name) {
    RepeatingConfigReader reader = new RepeatingConfigReader("xdbpdiconfigs", resolveTemplatedKeys(
        Arrays.asList(QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, QUERY_XDBPDI_SCHEMA_TEMPLATE, QUERY_XDBPDI_TEMPLATE_TEMPLATE),
        name));
    List<Map<String, String>> xdbPdiCfgs = reader.read(configuration);
    List<XdbPdiConfig> xdbPdis = new ArrayList<>();
    for (Map<String, String> cfg : xdbPdiCfgs) {
      XdbPdiConfig xdbPdi = new XdbPdiConfig();
      xdbPdi.setEntityPath(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, name)));
      xdbPdi.setSchema(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_SCHEMA_TEMPLATE, name)));
      xdbPdi.setTemplate(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_TEMPLATE_TEMPLATE, name)));
      xdbPdi.setOperands(createOperands(name, xdbPdi.getSchema()));
      xdbPdis.add(xdbPdi);
    }
    return xdbPdis;
  }

  private List<Operand> createOperands(String name, String schema) {
    RepeatingConfigReader reader =
        new RepeatingConfigReader("operands", resolveTemplatedKeys(Arrays.asList(QUERY_XDBPDI_OPERAND_NAME,
            QUERY_XDBPDI_OPERAND_PATH, QUERY_XDBPDI_OPERAND_TYPE, QUERY_XDBPDI_OPERAND_INDEX), name, schema));
    List<Map<String, String>> operandCfgs = reader.read(configuration);
    List<Operand> operands = new ArrayList<>();
    for (Map<String, String> cfg : operandCfgs) {
      Operand operand = new Operand();
      operand.setIndex(Boolean.parseBoolean(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_INDEX, name, schema))));
      operand.setType(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_TYPE, name, schema)));
      operand.setName(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_NAME, name, schema)));
      operand.setPath(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_PATH, name, schema)));
      operands.add(operand);
    }
    return operands;
  }

  private List<String> resolveTemplatedKeys(List<String> templatedKeys, Object... vars) {
    return templatedKeys.stream()
        .map(key -> resolveTemplatedKey(key, vars))
        .collect(Collectors.toList());
  }

  private String resolveTemplatedKey(String key, Object... vars) {
    return String.format(key, vars);
  }

  private Holding createHolding(String holdingName) {
    Holding result = new Holding();
    result.setName(holdingName);
    result.setAllStores(configurationState.getStoreUri());
    IngestConfig ingestConfig = new IngestConfig();
    ingestConfig.setIngest(configurationState.getIngestUri());
    result.getIngestConfigs()
        .add(ingestConfig);
    result.getIngestNodes()
      .add(configurationState.getIngestNodeUri());
    SubPriority priority = new SubPriority();
    priority.setPriority(0);
    priority.setDeadline(100);
    result.getSubPriorities()
        .add(priority);
    priority = new SubPriority();
    priority.setPriority(1);
    priority.setDeadline(200);
    result.getSubPriorities()
        .add(priority);
    result.setXdbLibraryParent(configurationState.getLibraryUri());
    RetentionClass retentionClass = new RetentionClass();
    retentionClass.getPolicies()
        .add(configured(RETENTION_POLICY_NAME));
    result.getRetentionClasses()
        .add(retentionClass);
    PdiConfig pdiConfig = new PdiConfig();
    pdiConfig.setPdi(configurationState.getPdiUri());
    pdiConfig.setSchema(configured(PDI_SCHEMA_NAME));
    result.getPdiConfigs()
        .add(pdiConfig);
    return result;
  }

  private void ensureResultConfigurationHelper() throws IOException {
    String name = configuration.getOrDefault(RESULT_HELPER_NAME, DEFAULT_RESULT_HELPER_NAME);
    ResultConfigurationHelpers helpers = restClient.follow(configurationState.getApplication(),
        LINK_RESULT_CONFIGURATION_HELPERS, ResultConfigurationHelpers.class);
    ResultConfigurationHelper helper = helpers.byName(name);
    if (helper == null) {
      createItem(helpers, createResultConfigurationHelper(name));
      helper = restClient.refresh(helpers)
          .byName(name);
      Objects.requireNonNull(helper, "Could not create Result configuration helper");
    }
    configurationState.setResultConfigHelperUri(helper.getSelfUri());
  }

  private ResultConfigurationHelper createResultConfigurationHelper(String name) {
    ResultConfigurationHelper helper = new ResultConfigurationHelper();
    helper.setName(name);
    helper.setResultSchema(createResultSchema(name));
    return helper;
  }

  private List<String> createResultSchema(String name) {
    String schema = configuration.get(resolveTemplatedKey(RESULT_HELPER_SCHEMA_TEMPLATE, name));
    List<String> schemas = new ArrayList<>();
    schemas.add(schema);
    return schemas;
  }

  private void ensureSearch() throws IOException {
    String name = configuration.get(SEARCH_NAME);
    Searches searches = restClient.follow(configurationState.getApplication(), LINK_SEARCHES, Searches.class);
    configurationState.setSearch(searches.byName(name));
    if (configurationState.getSearch() == null) {
      createItem(searches, createSearch(name));
      configurationState.setSearch(restClient.refresh(searches)
        .byName(name));
      Objects.requireNonNull(configurationState.getSearch(), "Could not create Search");
    }
  }

  private Search createSearch(String name) {
    Search search = new Search();
    search.setName(name);
    search.setDescription(configuration.get(SEARCH_DESCRIPTION));
    search.setNestedSearch(configuration.get(SEARCH_NESTED).equals("false") ? false : true);
    search.setState(configuration.get(SEARCH_STATE));
    search.setInUse(configuration.get(SEARCH_INUSE).equals("false") ? false : true);
    search.setAic(configurationState.getAicUri());
    search.setQuery(configurationState.getQueryUris().get(0));
    return search;
  }

  private void ensureSearchComposition() throws IOException {
    String name = configuration.get(SEARCH_COMPOSITION_NAME);
    SearchCompositions compositions = restClient.follow(configurationState.getSearch(), LINK_SEARCH_COMPOSITIONS, SearchCompositions.class);
    configurationState.setSearchComposition(compositions.byName(name));
    if (configurationState.getSearchComposition() == null) {
      createItem(compositions, LINK_SELF, createSearchComposition(name));
      configurationState.setSearchComposition(restClient.refresh(compositions)
          .byName(name));
      Objects.requireNonNull(configurationState.getSearchComposition(), "Could not create Search compostion");
    }
    createXForm();
  }

  private SearchComposition createSearchComposition(String name) {
    SearchComposition composition = new SearchComposition();
    composition.setName(name);
    composition.setSearchName(SEARCH_NAME);
    return composition;
  }

  private void createXForm() throws IOException {
    XForms xFroms = restClient.follow(configurationState.getSearchComposition(), LINK_XFORMS, XForms.class);
    XForm xForm = new XForm();
    configurationState.setxForm(createItem(xFroms, LINK_SELF, xForm));
      Objects.requireNonNull(configurationState.getxForm(), "Could not create XForm");
  }

  protected void cacheAipsUri() {
    aipsUri = configurationState.getApplication().getUri(LINK_AIPS);
  }

  protected void cacheDipUris() throws IOException {
    Aics aics = restClient.follow(configurationState.getApplication(), LINK_AICS, Aics.class);
    dipUrisByAicName = new HashMap<String, String>();
    if (aics != null) {
      aics.getItems()
        .forEach(aic -> dipUrisByAicName.put(aic.getName(), aic.getUri(LINK_DIP)));
    }
  }

  @Override
  public String ingest(InputStream sip) throws IOException {
    Objects.requireNonNull(aipsUri, "Did you forget to call configure()?");

    ReceptionResponse response = restClient.post(aipsUri, ReceptionResponse.class, new TextPart("format", "sip_zip"),
        new BinaryPart("sip", sip, "IASIP.zip"));
    return restClient.put(response.getUri(LINK_INGEST), IngestionResponse.class)
        .getAipId();
  }

  @Override
  public QueryResult query(SearchQuery query, String aic, String schema, int pageSize) throws IOException {
    Objects.requireNonNull(dipUrisByAicName, "Did you forget to call configure()?");

    String formattedQuery = queryFormatter.format(query);
    String baseUri = dipUrisByAicName.get(aic);
    Objects.requireNonNull(baseUri, String.format("No DIP resource found for AIC %s", aic));
    String queryUri = restClient.uri(baseUri)
        .addParameter("query", formattedQuery)
        .addParameter("schema", schema)
        .addParameter("size", String.valueOf(pageSize))
        .build();
    return restClient.get(queryUri, queryResultFactory);
  }

}
