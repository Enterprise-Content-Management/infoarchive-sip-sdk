/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configurer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.emc.ia.sdk.sip.client.dto.Aic;
import com.emc.ia.sdk.sip.client.dto.Aics;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Contents;
import com.emc.ia.sdk.sip.client.dto.Criterion;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Databases;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolder;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolders;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoots;
import com.emc.ia.sdk.sip.client.dto.Holding;
import com.emc.ia.sdk.sip.client.dto.Holdings;
import com.emc.ia.sdk.sip.client.dto.Ingest;
import com.emc.ia.sdk.sip.client.dto.IngestConfig;
import com.emc.ia.sdk.sip.client.dto.IngestNode;
import com.emc.ia.sdk.sip.client.dto.IngestNodes;
import com.emc.ia.sdk.sip.client.dto.Ingests;
import com.emc.ia.sdk.sip.client.dto.Libraries;
import com.emc.ia.sdk.sip.client.dto.Library;
import com.emc.ia.sdk.sip.client.dto.Namespace;
import com.emc.ia.sdk.sip.client.dto.Operand;
import com.emc.ia.sdk.sip.client.dto.Pdi;
import com.emc.ia.sdk.sip.client.dto.PdiConfig;
import com.emc.ia.sdk.sip.client.dto.PdiSchema;
import com.emc.ia.sdk.sip.client.dto.PdiSchemas;
import com.emc.ia.sdk.sip.client.dto.Pdis;
import com.emc.ia.sdk.sip.client.dto.Queries;
import com.emc.ia.sdk.sip.client.dto.Query;
import com.emc.ia.sdk.sip.client.dto.Quota;
import com.emc.ia.sdk.sip.client.dto.Quotas;
import com.emc.ia.sdk.sip.client.dto.ReceiverNode;
import com.emc.ia.sdk.sip.client.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.client.dto.ResultConfigurationHelper;
import com.emc.ia.sdk.sip.client.dto.ResultConfigurationHelpers;
import com.emc.ia.sdk.sip.client.dto.RetentionClass;
import com.emc.ia.sdk.sip.client.dto.RetentionPolicies;
import com.emc.ia.sdk.sip.client.dto.RetentionPolicy;
import com.emc.ia.sdk.sip.client.dto.Search;
import com.emc.ia.sdk.sip.client.dto.SearchComposition;
import com.emc.ia.sdk.sip.client.dto.SearchCompositions;
import com.emc.ia.sdk.sip.client.dto.Searches;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Sip;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolder;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolders;
import com.emc.ia.sdk.sip.client.dto.SpaceRootLibraries;
import com.emc.ia.sdk.sip.client.dto.SpaceRootLibrary;
import com.emc.ia.sdk.sip.client.dto.Spaces;
import com.emc.ia.sdk.sip.client.dto.Store;
import com.emc.ia.sdk.sip.client.dto.Stores;
import com.emc.ia.sdk.sip.client.dto.SubPriority;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.sip.client.dto.XForm;
import com.emc.ia.sdk.sip.client.dto.XdbPdiConfig;
import com.emc.ia.sdk.sip.client.dto.export.ExportConfiguration;
import com.emc.ia.sdk.sip.client.dto.export.ExportConfigurations;
import com.emc.ia.sdk.sip.client.dto.export.ExportPipeline;
import com.emc.ia.sdk.sip.client.dto.export.ExportPipelines;
import com.emc.ia.sdk.sip.client.dto.export.ExportTransformation;
import com.emc.ia.sdk.sip.client.dto.export.ExportTransformations;
import com.emc.ia.sdk.sip.client.dto.result.Column;
import com.emc.ia.sdk.sip.client.dto.result.Column.DataType;
import com.emc.ia.sdk.sip.client.dto.result.Column.DefaultSort;
import com.emc.ia.sdk.sip.client.dto.result.ResultMaster;
import com.emc.ia.sdk.sip.client.dto.result.searchconfig.AllSearchComponents;
import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.sip.client.rest.RestCache;
import com.emc.ia.sdk.support.NewInstance;
import com.emc.ia.sdk.support.RepeatingConfigReader;
import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.datetime.DefaultClock;
import com.emc.ia.sdk.support.http.BinaryPart;
import com.emc.ia.sdk.support.http.HttpClient;
import com.emc.ia.sdk.support.http.HttpException;
import com.emc.ia.sdk.support.http.MediaTypes;
import com.emc.ia.sdk.support.http.TextPart;
import com.emc.ia.sdk.support.http.apache.ApacheHttpClient;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.AuthenticationStrategy;
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.RestClient;


@SuppressWarnings("PMD.ExcessiveClassLength")
public class PropertyBasedConfigurer implements InfoArchiveConfigurer, InfoArchiveLinkRelations,
    InfoArchiveConfiguration {

  private static final String TYPE_EXPORT_PIPELINE = "export-pipeline";
  private static final int MAX_RETRIES = 5;
  private static final int RETRY_MS = 500;
  private static final int WAIT_RACE_CONDITION_MS = 2000;
  private static final String FORMAT_XML = "xml";
  private static final String FORMAT_XSD = "xsd";
  private static final String WORKING_FOLDER_NAME = "working/";
  private static final String RECEIVER_NODE_NAME = "receiver_node_01";
  private static final String INGEST_NAME = "ingest";
  private static final String INGEST_NODE_NAME = "ingest_node_01";
  private static final String DEFAULT_STORE_NAME = "filestore_01";
  private static final String DEFAULT_RESULT_HELPER_NAME = "result_helper";

  private final RestCache configurationState = new RestCache();

  private Map<String, String> configuration;
  private RestClient restClient;
  private final Clock clock;

  public PropertyBasedConfigurer(Map<String, String> configuration) {
    this(null, configuration);
  }

  public PropertyBasedConfigurer(RestClient restClient, Map<String, String> configuration) {
    this(restClient, new DefaultClock(), configuration);
  }

  public PropertyBasedConfigurer(RestClient restClient, Clock clock, Map<String, String> configuration) {
    this.restClient = restClient;
    this.clock = clock;
    this.configuration = configuration;
  }

  @Override
  public void configure() {
    try {
      initRestClient();
      ensureTenant();
      ensureFederation();
      ensureDatabase();
      ensureFileSystemRoot();
      ensureTenantLevelExportPipelines();
      ensureTenantLevelExportConfigurations();
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
      ensureExportPipelines();
      ensureExportConfigurations();
      ensureExportTransformations();
      ensureSearch();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  protected RestCache getConfigurationState() {
    return configurationState;
  }

  protected Map<String, String> setConfiguration(Map<String, String> config) {
    return configuration = config;
  }

  private void initRestClient() throws IOException {
    if (restClient == null) {
      HttpClient httpClient =
          NewInstance.fromConfiguration(configuration, HTTP_CLIENT_CLASSNAME, ApacheHttpClient.class.getName())
            .as(HttpClient.class);
      AuthenticationStrategy authentication = new AuthenticationStrategyFactory(configuration)
                                                  .getAuthenticationStrategy(() -> httpClient, () -> clock);
      restClient = new RestClient(httpClient);
      restClient.init(authentication);
    }
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
    return createItem(collection, item, LINK_ADD, LINK_SELF);
  }

  public <T> T createItem(LinkContainer collection, T item, String... linkRelations) throws IOException {
    return perform(() -> restClient.createCollectionItem(collection, item, linkRelations));
  }

  private <T> T perform(Operation<T> operation) throws IOException {
    int retry = 0;
    while (retry < MAX_RETRIES) {
      retry++;
      try {
        return operation.perform();
      } catch (IOException e) {
        if (isTemporarilyUnavailable(e)) {
          clock.sleep(RETRY_MS * retry, TimeUnit.MILLISECONDS);
        } else if (isDuplicateObjectException(e.getMessage())) {
          clock.sleep(WAIT_RACE_CONDITION_MS, TimeUnit.MILLISECONDS);
          break;
        } else {
          throw e;
        }
      }
    }
    return null;
  }

  private boolean isTemporarilyUnavailable(IOException exception) {
    if (exception instanceof HttpException) {
      HttpException httpException = (HttpException)exception;
      return httpException.getStatusCode() == 503;
    }
    return false;
  }

  private boolean isDuplicateObjectException(String error) {
    return error.contains("DUPLICATE_KEY") || error.contains("NAME_ALREADY_EXISTS");
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
      createItem(spaces, createSpace(spaceName));
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
    // Create the default file system folder.
    FileSystemFolder defaultFolder = ensureFileSystemFolder(configured(HOLDING_NAME));
    configurationState.setFileSystemFolderUri(defaultFolder.getSelfUri());

    // Any extra folders
    String rawFolderNames = configuration.get(FILE_SYSTEM_FOLDER);
    if (rawFolderNames != null && !rawFolderNames.isEmpty()) {
      String[] folderNames = rawFolderNames.split(",");
      for (String folderName : folderNames) {
        ensureFileSystemFolder(folderName.trim());
      }
    }
  }

  private FileSystemFolder ensureFileSystemFolder(String name) throws IOException {
    FileSystemFolders fileSystemFolders =
        restClient.follow(configurationState.getSpaceRootFolder(), LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class);
    FileSystemFolder fileSystemFolder = fileSystemFolders.byName(name);
    if (fileSystemFolder == null) {
      createItem(fileSystemFolders, createFileSystemFolder(name));
      fileSystemFolder = restClient.refresh(fileSystemFolders)
        .byName(name);
      Objects.requireNonNull(fileSystemFolder, "Could not create file system folder " + name);
    }
    configurationState.setObjectUri("filesystemfolder", name, fileSystemFolder.getSelfUri());
    return fileSystemFolder;
  }

  private FileSystemFolder createFileSystemFolder(String name) {
    FileSystemFolder result = new FileSystemFolder();
    result.setName(name);
    result.setParentSpaceRootFolder(configurationState.getSpaceRootFolder()
      .getSelfUri());
    result.setSubPath("stores/" + DEFAULT_STORE_NAME);
    return result;
  }

  private void ensureStore() throws IOException {
    Store defaultStore = ensureStore(DEFAULT_STORE_NAME, null, configurationState.getFileSystemFolderUri());
    configurationState.setStoreUri(defaultStore.getSelfUri());

    RepeatingConfigReader reader =
        new RepeatingConfigReader("stores", Arrays.asList(STORE_NAME, STORE_FOLDER, STORE_STORETYPE));

    List<Map<String, String>> storeConfigurations = reader.read(configuration);
    for (Map<String, String> cfg : storeConfigurations) {
      ensureStore(cfg.get(STORE_NAME), cfg.get(STORE_STORETYPE),
          configurationState.getObjectUri("filesystemfolder", cfg.get(STORE_FOLDER)));
    }
  }

  private Store ensureStore(String name, String storeType, String fileSystemFolderUri) throws IOException {
    Stores stores = restClient.follow(configurationState.getApplication(), LINK_STORES, Stores.class);
    Store store = stores.byName(name);
    if (store == null) {
      createItem(stores, createStore(name, storeType, fileSystemFolderUri));
      store = restClient.refresh(stores)
        .byName(name);
      Objects.requireNonNull(store, "Could not create store");
    }
    configurationState.setObjectUri("store", name, store.getSelfUri());
    return store;
  }

  private Store createStore(String storeName, String storeType, String fileSystemFolderUri) {
    Store store = new Store();
    store.setName(storeName);
    if (storeType != null && !storeType.isEmpty()) {
      store.setStoreType(storeType);
    }
    store.setFileSystemFolder(fileSystemFolderUri);
    return store;
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

  private void ensureTenantLevelExportPipelines() throws IOException {
    String defaultPipelineName = "search-results-csv-gzip";
    ExportPipelines pipelines =
        restClient.follow(configurationState.getTenant(), LINK_EXPORT_PIPELINE, ExportPipelines.class);
    ExportPipeline pipeline = pipelines.byName(defaultPipelineName);
    if (pipeline == null) {
      pipeline = new ExportPipeline();
      pipeline.setName(defaultPipelineName);
      pipeline.setDescription("gzip envelope for csv export");
      pipeline.setIncludesContent(false);
      pipeline.setInputFormat("ROW_COLUMN");
      pipeline.setOutputFormat("csv");
      pipeline.setEnvelopeFormat("gz");
      pipeline.setType("XPROC");
      pipeline.setComposite(true);
      pipeline.setContent(
          "<p:declare-step version=\"2.0\" xmlns:p=\"http://www.w3.org/ns/xproc\" xmlns:ia=\"http://infoarchive.emc.com/xproc\">\n    <p:input port=\"source\" sequence=\"true\"/>\n    <ia:search-results-csv/>\n    <ia:gzip/>\n    <ia:store-export-result format=\"csv\"/>\n</p:declare-step>");

      createItem(pipelines, pipeline);
      pipeline = restClient.refresh(pipelines)
        .byName(defaultPipelineName);
      Objects.requireNonNull(pipeline, "Could not create export pipeline");
    }
    configurationState.setObjectUri(TYPE_EXPORT_PIPELINE, defaultPipelineName, pipeline.getSelfUri());
  }

  private void ensureTenantLevelExportConfigurations() throws IOException {
    String defaultName = "gzip_csv";
    ExportConfigurations configurations =
        restClient.follow(configurationState.getTenant(), LINK_EXPORT_CONFIG, ExportConfigurations.class);
    ExportConfiguration exportConfiguration = configurations.byName(defaultName);
    if (exportConfiguration == null) {
      exportConfiguration = new ExportConfiguration();
      exportConfiguration.setName(defaultName);
      exportConfiguration.setDescription("configurations");
      exportConfiguration.setExportType("ASYNCHRONOUS");
      exportConfiguration.setPipeline(configurationState.getObjectUri(TYPE_EXPORT_PIPELINE, "search-results-csv-gzip"));
      createItem(configurations, exportConfiguration);
      exportConfiguration = restClient.refresh(configurations)
        .byName(defaultName);
      Objects.requireNonNull(exportConfiguration, "Could not create export configuration");
    }
    configurationState.setObjectUri("export-configuration", defaultName, exportConfiguration.getSelfUri());

  }

  private void ensureExportPipelines() throws IOException {
    String rawPipelineNames = configuration.get(EXPORT_PIPELINE_NAME);
    if (rawPipelineNames != null && !rawPipelineNames.isEmpty()) {
      String[] pipelineNames = rawPipelineNames.split(",");
      ExportPipelines pipelines =
        restClient.follow(configurationState.getApplication(), LINK_EXPORT_PIPELINE, ExportPipelines.class);
      for (String pipelineName : pipelineNames) {
        ExportPipeline pipeline = pipelines.byName(pipelineName);
        if (pipeline == null) {
          createItem(pipelines, createExportPipeline(pipelineName));
          pipeline = restClient.refresh(pipelines)
            .byName(pipelineName);
          Objects.requireNonNull(pipeline, "Could not create export pipeline");
        }
        configurationState.setObjectUri(TYPE_EXPORT_PIPELINE, pipelineName, pipeline.getSelfUri());
      }
    }
  }

  private void ensureExportConfigurations() throws IOException {
    String rawConfigurationNames = configuration.get(EXPORT_CONFIG_NAME);
    if (rawConfigurationNames != null && !rawConfigurationNames.isEmpty()) {
      String[] configurationNames = rawConfigurationNames.split(",");
      ExportConfigurations configurations =
        restClient.follow(configurationState.getApplication(), LINK_EXPORT_CONFIG, ExportConfigurations.class);
      for (String configurationName : configurationNames) {
        ExportConfiguration exportConfiguration = configurations.byName(configurationName);
        if (exportConfiguration == null) {
          createItem(configurations, createExportConfiguration(configurationName));
          exportConfiguration = restClient.refresh(configurations)
            .byName(configurationName);
          Objects.requireNonNull(configuration, "Could not create export configuration");
        }
        configurationState.setObjectUri("export-configuration", configurationName, exportConfiguration.getSelfUri());
      }
    }
  }

  private void ensureExportTransformations() throws IOException {
    String rawTransformationNames = configuration.get(EXPORT_TRANSFORMATION_NAME);
    if (rawTransformationNames != null && !rawTransformationNames.isEmpty()) {
      String[] transformationNames = rawTransformationNames.split(",");
      ExportTransformations transformations =
        restClient.follow(configurationState.getApplication(), LINK_EXPORT_TRANSFORMATION, ExportTransformations.class);
      for (String transformationName : transformationNames) {
        ExportTransformation exportTransformation = transformations.byName(transformationName);
        if (exportTransformation == null) {
          createItem(transformations, createExportTransformation(transformationName));
          exportTransformation = restClient.refresh(transformations)
                                  .byName(transformationName);
          Objects.requireNonNull(configuration, "Could not create export transformation");
        }
        configurationState.setObjectUri("export-transformation", transformationName, exportTransformation.getSelfUri());
      }
    }
  }

  private ExportConfiguration createExportConfiguration(String name) {
    ExportConfiguration conf = new ExportConfiguration();
    conf.setName(name);
    conf.setExportType(getString(EXPORT_CONFIG_TYPE_TEMPLATE, name));
    conf.setPipeline(
        configurationState.getObjectUri(TYPE_EXPORT_PIPELINE, getString(EXPORT_CONFIG_PIPELINE_TEMPLATE, name)));
    return conf;
  }

  private ExportPipeline createExportPipeline(String name) {
    ExportPipeline pipeline = new ExportPipeline();
    pipeline.setName(name);
    pipeline.setComposite(getBoolean(EXPORT_PIPELINE_COMPOSITE_TEMPLATE, name));
    pipeline.setContent(getString(EXPORT_PIPELINE_CONTENT_TEMPLATE, name));
    pipeline.setDescription(getString(EXPORT_PIPELINE_DESCRIPTION_TEMPLATE, name));
    pipeline.setEnvelopeFormat(getString(EXPORT_PIPELINE_ENVELOPE_FORMAT_TEMPLATE, name));
    pipeline.setIncludesContent(getBoolean(EXPORT_PIPELINE_INCLUDES_CONTENT_TEMPLATE, name));
    pipeline.setInputFormat(getString(EXPORT_PIPELINE_INPUT_FORMAT_TEMPLATE, name));
    pipeline.setOutputFormat(getString(EXPORT_PIPELINE_OUTPUT_FORMAT_TEMPLATE, name));
    pipeline.setType(getString(EXPORT_PIPELINE_TYPE_TEMPLATE, name));
    return pipeline;
  }

  private ExportTransformation createExportTransformation(String name) {
    ExportTransformation transformation = new ExportTransformation();
    transformation.setName(name);
    transformation.setDescription(getString(EXPORT_TRANSFORMATION_DESCRIPTION_TEMPLATE, name));
    transformation.setType(getString(EXPORT_TRANSFORMATION_TYPE_TEMPLATE, name));
    transformation.setMainPath(getString(EXPORT_TRANSFORMATION_MAIN_PATH_TEMPLATE, name));
    return transformation;
  }

  private boolean getBoolean(String key, String name) {
    return Boolean.parseBoolean(getString(key, name));
  }

  private String getString(String key, String name) {
    return configuration.get(resolveTemplatedKey(key, name));
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
      createItem(retentionPolicies, createRetentionPolicy(name));
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
      perform(() -> restClient.post(state.getUri(LINK_CONTENTS), null,
            new TextPart("content", MediaTypes.HAL, "{ \"format\": \"" + format + "\" }"),
            new BinaryPart("file", stream, configurationName)));
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
    configurationState.setHoldingUri(holding.getSelfUri());
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
    configurationState.setAicUriByName(name, aic.getSelfUri());

  }

  private Aic createAic(String name) {
    Aic aic = new Aic();
    aic.setName(name);
    aic.setCriterias(createCriteria());
    aic.getHoldings()
      .add(configurationState.getHoldingUri());
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

      String minAttr = cfg.get(CRITERIA_PKEYMINATTR)
        .trim();
      if (!minAttr.isEmpty()) {
        criterion.setpKeyMinAttr(minAttr);
      }

      String maxAttr = cfg.get(CRITERIA_PKEYMAXATTR)
        .trim();
      if (!maxAttr.isEmpty()) {
        criterion.setpKeyMaxAttr(maxAttr);
      }

      String valueAttr = cfg.get(CRITERIA_PKEYVALUESATTR)
        .trim();
      if (!valueAttr.isEmpty()) {
        criterion.setpKeyValuesAttr(valueAttr);
      }
      criterion.setType(cfg.get(CRITERIA_TYPE));
      criteria.add(criterion);
    }

    return criteria;
  }

  private void ensureQuery() throws IOException {
    String rawQueryNames = configuration.get(QUERY_NAME);
    List<String> queryUris = new ArrayList<>();
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
        configurationState.setQueryUriByName(queryName, query.getSelfUri());
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
    if (configuration.containsKey(resolveTemplatedKey(RESULT_HELPER_XML, name))) {
      ResultConfigurationHelpers helpers = restClient.follow(configurationState.getApplication(),
          LINK_RESULT_CONFIGURATION_HELPERS, ResultConfigurationHelpers.class);
      ResultConfigurationHelper helper = helpers.byName(name);
      if (helper == null) {
        createItem(helpers, createResultConfigurationHelper(name));
        helper = restClient.refresh(helpers)
          .byName(name);
        Objects.requireNonNull(helper, "Could not create Result configuration helper");
      }
      ensureContents(helper, resolveTemplatedKey(RESULT_HELPER_XML, name), FORMAT_XML);

      configurationState.setResultConfigHelperUri(helper.getSelfUri());
    }
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
    String[] searchNames = configuration.get(SEARCH_NAME)
      .split(",");
    for (String name : searchNames) {
      Searches searches = restClient.follow(configurationState.getApplication(), LINK_SEARCHES, Searches.class);
      configurationState.setSearch(searches.byName(name));
      if (configurationState.getSearch() == null) {
        createItem(searches, createSearch(name));
        configurationState.setSearch(restClient.refresh(searches)
          .byName(name));
        Objects.requireNonNull(configurationState.getSearch(), "Could not create Search");

        ensureSearchComposition(name);
      }
    }
  }

  private Search createSearch(String name) {
    Search search = new Search();
    search.setName(name);
    search.setDescription(configuration.get(resolveTemplatedKey(SEARCH_DESCRIPTION, name)));
    search.setNestedSearch(Boolean.parseBoolean(configuration.get(resolveTemplatedKey(SEARCH_NESTED, name))));
    search.setState(configuration.get(resolveTemplatedKey(SEARCH_STATE, name)));
    search.setInUse(Boolean.parseBoolean(configuration.get(resolveTemplatedKey(SEARCH_INUSE, name))));
    search.setAic(configurationState.getAicUriByName(configuration.get(resolveTemplatedKey(SEARCH_AIC, name))));
    search.setQuery(configurationState.getQueryUriByName(configuration.get(resolveTemplatedKey(SEARCH_QUERY, name))));
    return search;
  }

  private void ensureSearchComposition(String searchName) throws IOException {
    String name = configuration.get(resolveTemplatedKey(SEARCH_COMPOSITION_NAME, searchName));
    if (name == null || name.isEmpty()) {
      name = "Set 1";
    }
    SearchCompositions compositions =
        restClient.follow(configurationState.getSearch(), LINK_SEARCH_COMPOSITIONS, SearchCompositions.class);
    configurationState.setSearchComposition(compositions.byName(name));
    if (configurationState.getSearchComposition() == null) {
      createItem(compositions, createSearchComposition(name), LINK_SELF);
      SearchComposition composition = restClient.refresh(compositions)
        .byName(name);
      Objects.requireNonNull(composition, "Could not create Search compostion");

      ensureAllSearchComponents(searchName, composition);
    }
  }

  private SearchComposition createSearchComposition(String name) {
    SearchComposition composition = new SearchComposition();
    composition.setName(name);
    composition.setSearchName(SEARCH_NAME);
    return composition;
  }

  private void ensureAllSearchComponents(String searchName, SearchComposition composition) throws IOException {

    AllSearchComponents components = new AllSearchComponents();
    components.setSearchComposition(composition);
    components.setXform(createXForm(searchName, composition.getName()));
    components.setResultMaster(createResultMaster(searchName, composition.getName()));
    SearchComposition updatedComposition =
        restClient.put(composition.getUri(LINK_ALLCOMPONENTS), SearchComposition.class, components);

    Objects.requireNonNull(updatedComposition, "Failed to update search composition");

  }

  private XForm createXForm(String searchName, String compositionName) throws IOException {
    XForm xForm = new XForm();
    String form = configuration.get(resolveTemplatedKey(SEARCH_COMPOSITION_XFORM, searchName, compositionName));
    Objects.requireNonNull(form, "XForm XML is missing.");
    xForm.setForm(form);
    return xForm;
  }

  private ResultMaster createResultMaster(String searchName, String compositionName) throws IOException {
    ResultMaster resultMaster = new ResultMaster();

    resultMaster.setNamespaces(createNamespaces(configuration.get(resolveTemplatedKey(SEARCH_QUERY, searchName))));

    List<Column> columns = resultMaster.getDefaultTab()
      .getColumns();

    resultMaster.getDefaultTab()
      .setExportEnabled(getBoolean(SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_ENABLED_TEMPLATE, searchName));
    resultMaster.getDefaultTab()
      .getExportConfigurations()
      .addAll(uriFromNamesAndType("export-configuration",
          getStrings(SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_CONFIG_TEMPLATE, searchName)));

    RepeatingConfigReader reader = new RepeatingConfigReader("maincolumns",
        resolveTemplatedKeys(Arrays.asList(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME,
            SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL, SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH,
            SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE, SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT), searchName,
            compositionName));
    List<Map<String, String>> columnCfgs = reader.read(configuration);

    for (Map<String, String> columnCfg : columnCfgs) {
      String name =
          columnCfg.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME, searchName, compositionName));
      String label =
          columnCfg.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL, searchName, compositionName));
      String path =
          columnCfg.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH, searchName, compositionName));
      DataType dataType = DataType.valueOf(
          columnCfg.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE, searchName, compositionName)));
      DefaultSort sortOrder = DefaultSort.valueOf(
          columnCfg.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT, searchName, compositionName)));

      Column column = Column.fromSchema(name, label, path, dataType, sortOrder);
      columns.add(column);
    }
    return resultMaster;
  }

  private List<String> uriFromNamesAndType(String type, Collection<String> strings) {
    return strings.stream()
      .map(it -> configurationState.getObjectUri(type, it))
      .collect(Collectors.toList());
  }

  private List<String> getStrings(String key, String variable) {
    String rawString = getString(key, variable);
    if (rawString != null) {
      String[] values = rawString.split(",");
      List<String> normalizedStrings = new ArrayList<>();
      for (String value : values) {
        if (value != null) {
          String normalizedValue = value.trim();
          if (!normalizedValue.isEmpty()) {
            normalizedStrings.add(normalizedValue);
          }
        }
      }
      return normalizedStrings;
    }
    return Collections.emptyList();
  }


  @FunctionalInterface
  interface Operation<T> {
    T perform() throws IOException;
  }

}
