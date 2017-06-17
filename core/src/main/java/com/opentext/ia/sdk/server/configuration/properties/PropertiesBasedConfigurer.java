/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.properties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.dto.*;
import com.opentext.ia.sdk.dto.export.*;
import com.opentext.ia.sdk.dto.export.ExportConfiguration.DefaultOption;
import com.opentext.ia.sdk.dto.export.ExportConfiguration.Transformation;
import com.opentext.ia.sdk.dto.result.AllSearchComponents;
import com.opentext.ia.sdk.dto.result.Column;
import com.opentext.ia.sdk.dto.result.Column.DataType;
import com.opentext.ia.sdk.dto.result.Column.DefaultSort;
import com.opentext.ia.sdk.dto.result.ResultMaster;
import com.opentext.ia.sdk.server.configuration.ApplicationConfigurer;
import com.opentext.ia.sdk.server.configuration.ApplicationResourcesCache;
import com.opentext.ia.sdk.server.configuration.yaml.YamlBasedConfigurer;
import com.opentext.ia.sdk.support.RepeatingConfigReader;
import com.opentext.ia.sdk.support.http.BinaryPart;
import com.opentext.ia.sdk.support.http.HttpException;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.TextPart;
import com.opentext.ia.sdk.support.http.rest.LinkContainer;
import com.opentext.ia.sdk.support.http.rest.RestClient;
import com.opentext.ia.sdk.support.io.RuntimeIoException;


/**
 * Configure an InfoArchive application based on properties in a map. This is less convenient for repeating and/or
 * hierarchical configuration. For a more convenient way of specifying the configuration, see
 * {@linkplain YamlBasedConfigurer}.
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public class PropertiesBasedConfigurer implements ApplicationConfigurer, InfoArchiveLinkRelations,
    InfoArchiveConfigurationProperties {

  private static final String EXPORT_CONFIGURATION = "export-configuration";
  private static final String TYPE_EXPORT_PIPELINE = "export-pipeline";
  private static final String TYPE_EXPORT_TRANSFORMATION = "export-transformation";
  private static final int MAX_RETRIES = 5;
  private static final long RETRY_MS = 500;
  private static final long WAIT_RACE_CONDITION_MS = 2000;
  private static final String FORMAT_XML = "xml";
  private static final String FORMAT_XSD = "xsd";
  private static final String WORKING_FOLDER_NAME = "working/";
  private static final String RECEIVER_NODE_NAME = "receiver_node_01";
  private static final String INGEST_NAME = "ingest";
  private static final String INGEST_NODE_NAME = "ingest_node_01";
  private static final String DEFAULT_STORE_NAME = "filestore_01";
  private static final String DEFAULT_RESULT_HELPER_NAME = "result_helper";

  private final ApplicationResourcesCache cache = new ApplicationResourcesCache();
  private Map<String, String> configuration;
  private RestClient restClient;

  public PropertiesBasedConfigurer(Map<String, String> configuration) {
    this.configuration = configuration;
  }

  protected ApplicationResourcesCache getCache() {
    return cache;
  }

  protected RestClient getRestClient() {
    return restClient;
  }

  protected void setConfiguration(Map<String, String> configuration) {
    this.configuration = configuration;
  }

  @Override
  public void configure(ArchiveConnection connection) {
    try {
      initRestClient(connection);
      applyConfiguration();
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private void initRestClient(ArchiveConnection connection) throws IOException {
    restClient = connection.getRestClient();
    cache.setServices(restClient.get(connection.getBillboardUri(), Services.class));
  }

  protected void applyConfiguration() throws IOException {
    ensureTenant();
    ensureFederation();
    ensureDatabase();
    ensureFileSystemRoot();
    ensureStorageEndPoint();
    ensureContentAddressedStorage();
    ensureCustomStorage();
    ensureCryptoObject();
    ensureTenantLevelExportPipelines();
    ensureTenantLevelExportTransformations();
    ensureTenantLevelExportConfigurations();
    ensureApplication();
    ensureSpace();
    ensureSpaceRootLibrary();
    ensureSpaceRootFolder();
    ensureFileSystemFolder();
    ensureStores();
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
    ensureQueries();
    ensureResultConfigurationHelper();
    ensureExportPipelines();
    ensureExportTransformations();
    ensureExportConfigurations();
    ensureSearches();
    ensurePdiCrypto();
    ensureHoldingCrypto();
  }

  private void ensureTenant() throws IOException {
    cache.setTenant(Objects.requireNonNull(
        restClient.follow(cache.getServices(), LINK_TENANT, Tenant.class), "Missing tenant"));
  }

  private void ensureFederation() throws IOException {
    XdbFederation federation = ensureItem(cache.getServices(), LINK_FEDERATIONS, XdbFederations.class, FEDERATION_NAME,
        this::createFederation);
    cache.setFederation(federation);
  }

  private <T extends NamedLinkContainer> T ensureItem(LinkContainer collectionOwner,
      String collectionLinkRelation, Class<? extends ItemContainer<T>> collectionType, String objectName,
          Function<String, ? extends T> objectCreator) throws IOException {
    return ensureItem(collectionOwner, collectionLinkRelation, collectionType, objectName, objectCreator, false);
  }

  private <T extends NamedLinkContainer> T ensureItem(LinkContainer collectionOwner,
      String collectionLinkRelation, Class<? extends ItemContainer<T>> collectionType, String objectName,
          Function<String, ? extends T> objectCreator, boolean optional) throws IOException {
    String name = configuration.get(objectName);
    if (optional && name == null) {
      return null;
    }
    return ensureNamedItem(collectionOwner, collectionLinkRelation, collectionType, name, objectCreator);
  }

  private <T extends NamedLinkContainer> T ensureNamedItem(LinkContainer collectionOwner,
      String collectionLinkRelation, Class<? extends ItemContainer<T>> collectionType, String name,
      Function<String, ? extends T> objectCreator) throws IOException {
    ItemContainer<T> collection = restClient.follow(collectionOwner, collectionLinkRelation, collectionType);
    Objects.requireNonNull(collection, "Missing " + nameOf(collectionType));
    T result = collection.byName(name);
    if (result == null) {
      result = createItem(collection, name, objectCreator);
      if (result == null) {
        result = restClient.refresh(collection).byName(name);
      }
      Objects.requireNonNull(result, "Could not create item in " + nameOf(collectionType));
    }
    return result;
  }

  private String nameOf(Class<?> type) {
    return type.getSimpleName().toLowerCase(Locale.ENGLISH);
  }

  private XdbFederation createFederation(String name) {
    XdbFederation result = createObject(name, XdbFederation.class);
    result.setSuperUserPassword(configuration.get(FEDERATION_SUPERUSER_PASSWORD));
    result.setBootstrap(configuration.get(FEDERATION_BOOTSTRAP));
    return result;
  }

  private <T extends NamedLinkContainer> T createObject(String name, Class<T> type) {
    T result;
    try {
      result = type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException("Unable to create object of type " + type, e);
    }
    result.setName(name);
    return result;
  }

  private <T> T createItem(LinkContainer collection, String name, Function<String, T> newItem) throws IOException {
    return createItem(collection, newItem.apply(name), LINK_ADD, LINK_SELF);
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
        try {
          if (isTemporarilyUnavailable(e)) {
            TimeUnit.MILLISECONDS.sleep(RETRY_MS * retry);
          } else if (isDuplicateObjectException(e.getMessage())) {
            TimeUnit.MILLISECONDS.sleep(WAIT_RACE_CONDITION_MS);
            break;
          } else {
            throw e;
          }
        } catch (InterruptedException ignored) {
          Thread.currentThread().interrupt();
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
    cache.setDatabaseUri(ensureItem(cache.getFederation(), LINK_DATABASES, XdbDatabases.class,
        DATABASE_NAME, this::createDatabase).getSelfUri());
  }

  private XdbDatabase createDatabase(String name) {
    XdbDatabase result = createObject(name, XdbDatabase.class);
    result.setAdminPassword(configured(DATABASE_ADMIN_PASSWORD));
    return result;
  }

  private String configured(String name) {
    return Objects.requireNonNull(configuration.get(name), "Missing " + name);
  }

  private void ensureFileSystemRoot() throws IOException {
    FileSystemRoots existingFileSystemRoots = Objects.requireNonNull(restClient.follow(cache.getServices(),
        LINK_FILE_SYSTEM_ROOTS, FileSystemRoots.class), "Missing file system roots");
    cache.setFileSystemRootUri(existingFileSystemRoots.first().getSelfUri());
  }

  private void ensureApplication() throws IOException {
    Application application = ensureItem(cache.getTenant(), LINK_APPLICATIONS, Applications.class,
        APPLICATION_NAME, this::createApplication);
    cache.setApplication(application);
  }

  private Application createApplication(String name) {
    Application result = createObject(name, Application.class);
    result.setDescription(configuration.get(APPLICATION_DESCRIPTION));
    result.setCategory(configuration.get(APPLICATION_CATEGORY));
    return result;
  }

  private void ensureSpace() throws IOException {
    Space space = ensureNamedItem(cache.getApplication(), LINK_SPACES, Spaces.class, getApplicationName(),
        this::createSpace);
    cache.setSpace(space);
  }

  private Space createSpace(String name) {
    return createObject(name, Space.class);
  }

  private void ensureSpaceRootLibrary() throws IOException {
    cache.setSpaceRootLibrary(ensureItem(cache.getSpace(), LINK_SPACE_ROOT_LIBRARIES, SpaceRootXdbLibraries.class,
        HOLDING_NAME, this::createSpaceRootLibrary));
  }

  private SpaceRootXdbLibrary createSpaceRootLibrary(String name) {
    SpaceRootXdbLibrary result = createObject(name, SpaceRootXdbLibrary.class);
    result.setXdbDatabase(cache.getDatabaseUri());
    return result;
  }

  private void ensureSpaceRootFolder() throws IOException {
    SpaceRootFolder spaceRootFolder = ensureNamedItem(cache.getSpace(), LINK_SPACE_ROOT_FOLDERS, SpaceRootFolders.class,
        cache.getSpace().getName(), this::createSpaceRootFolder);
    cache.setSpaceRootFolder(spaceRootFolder);
  }

  private SpaceRootFolder createSpaceRootFolder(String name) {
    SpaceRootFolder result = createObject(name, SpaceRootFolder.class);
    result.setFileSystemRoot(cache.getFileSystemRootUri());
    return result;
  }

  private void ensureFileSystemFolder() throws IOException {
    // Create the default file system folder.
    FileSystemFolder defaultFolder = ensureFileSystemFolder(configured(HOLDING_NAME));
    cache.setFileSystemFolderUri(defaultFolder.getSelfUri());

    // Any extra folders
    processConfiguredItems(FILE_SYSTEM_FOLDER, (Processor)this::ensureFileSystemFolder);
  }

  private void processConfiguredItems(String configurationName, Processor processor) throws IOException {
    processItems(configuration.get(configurationName), processor);
  }

  private void processItems(String items, Processor processor) throws IOException {
    for (String item : commaSeparatedItems(items)) {
      processor.accept(item);
    }
  }

  private Collection<String> commaSeparatedItems(String items) {
    return isBlank(items) ? Collections.emptyList() : Arrays.stream(items.split("\\s*,\\s*"))
        .filter(this::isNotBlank)
        .collect(Collectors.toList());
  }

  private boolean isNotBlank(String text) {
    return !isBlank(text);
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private FileSystemFolder ensureFileSystemFolder(String name) throws IOException {
    FileSystemFolder fileSystemFolder = ensureNamedItem(cache.getSpaceRootFolder(), LINK_FILE_SYSTEM_FOLDERS,
        FileSystemFolders.class, name, this::createFileSystemFolder);
    return cacheObject("filesystemfolder", fileSystemFolder);
  }

  private FileSystemFolder createFileSystemFolder(String name) {
    FileSystemFolder result = createObject(name, FileSystemFolder.class);
    result.setParentSpaceRootFolder(cache.getSpaceRootFolder().getSelfUri());
    result.setSubPath("stores/" + DEFAULT_STORE_NAME);
    return result;
  }

  private <T extends NamedLinkContainer> T cacheObject(String type, T object) {
    cache.setObjectUri(type, object.getName(), object.getSelfUri());
    return object;
  }

  private void ensureStores() throws IOException {
    cache.setStoreUri(ensureStore(DEFAULT_STORE_NAME, null, cache.getFileSystemFolderUri(), null).getSelfUri());
    for (Map<String, String> cfg : getStoreConfigurations()) {
      ensureStore(cfg.get(STORE_NAME), cfg.get(STORE_STORETYPE), cache.getObjectUri("filesystemfolder",
          cfg.get(STORE_FOLDER)), cfg.get(STORE_TYPE));
    }
  }

  private Store ensureStore(String name, String storeType, String fileSystemFolderUri, String type)
      throws IOException {
    Store store = ensureNamedItem(cache.getApplication(), LINK_STORES, Stores.class, name,
        storeName -> createStore(storeName, storeType, fileSystemFolderUri, type));
    return cacheObject("store", store);
  }

  private Store createStore(String name, String storeType, String fileSystemFolderUri, String type) {
    Store result = createObject(name, Store.class);
    if (isNotBlank(storeType)) {
      result.setStoreType(storeType);
    }
    result.setFileSystemFolder(fileSystemFolderUri);
    if (isNotBlank(type)) {
      result.setType(type);
    }
    return result;
  }

  private Iterable<Map<String, String>> getStoreConfigurations() {
    return new RepeatingConfigReader("stores", Arrays.asList(STORE_NAME, STORE_FOLDER, STORE_STORETYPE, STORE_TYPE))
        .read(configuration);
  }

  private void ensureReceptionFolder() throws IOException {
    FileSystemFolder receptionFolder = ensureNamedItem(cache.getSpaceRootFolder(), LINK_FILE_SYSTEM_FOLDERS,
        FileSystemFolders.class, "reception-folder", this::createReceptionFolder);
    cache.setReceptionFolderUri(receptionFolder.getSelfUri());
  }

  private FileSystemFolder createReceptionFolder(String name) {
    FileSystemFolder result = createObject(name, FileSystemFolder.class);
    result.setParentSpaceRootFolder(cache.getSpaceRootFolder().getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + RECEIVER_NODE_NAME);
    return result;
  }

  private void ensureIngestionFolder() throws IOException {
    ensureNamedItem(cache.getSpaceRootFolder(), LINK_FILE_SYSTEM_FOLDERS, FileSystemFolders.class, "ingestion-folder",
        this::createIngestionFolder);
  }

  private FileSystemFolder createIngestionFolder(String name) {
    FileSystemFolder result = createObject(name, FileSystemFolder.class);
    result.setParentSpaceRootFolder(cache.getSpaceRootFolder().getSelfUri());
    result.setSubPath(WORKING_FOLDER_NAME + INGEST_NODE_NAME);
    return result;
  }

  private void ensureReceiverNode() throws IOException {
    ensureNamedItem(cache.getApplication(), LINK_RECEIVER_NODES, ReceiverNodes.class, RECEIVER_NODE_NAME,
        this::createReceiverNode);
  }

  private ReceiverNode createReceiverNode(String name) {
    ReceiverNode result = createObject(name, ReceiverNode.class);
    result.setWorkingDirectory(cache.getReceptionFolderUri());
    result.setLogsStore(cache.getStoreUri());
    result.getSips().add(new Sip());
    return result;
  }

  private void ensureTenantLevelExportPipelines() throws IOException {
    ExportPipeline exportPipeline = ensureNamedItem(cache.getTenant(), LINK_EXPORT_PIPELINE, ExportPipelines.class,
        "search-results-csv-gzip", this::createTenantLevelExportPipeline);
    cacheObject(TYPE_EXPORT_PIPELINE, exportPipeline);
  }

  private ExportPipeline createTenantLevelExportPipeline(String name) {
    ExportPipeline result = createObject(name, ExportPipeline.class);
    result.setDescription("gzip envelope for csv export");
    result.setIncludesContent(false);
    result.setInputFormat("ROW_COLUMN");
    result.setOutputFormat("csv");
    result.setEnvelopeFormat("gz");
    result.setType("XPROC");
    result.setComposite(true);
    result.setCollectionBasedExport(false);
    result.setContent("<p:declare-step version=\"2.0\" xmlns:p=\"http://www.w3.org/ns/xproc\" "
        + "xmlns:ia=\"http://infoarchive.emc.com/xproc\">\n    <p:input port=\"source\" sequence=\"true\"/>\n    "
        + "<ia:search-results-csv/>\n    <ia:gzip/>\n    <ia:store-export-result format=\"csv\"/>\n</p:declare-step>");
    return result;
  }

  private void ensureTenantLevelExportConfigurations() throws IOException {
    ExportConfiguration exportConfiguration = ensureNamedItem(cache.getTenant(), LINK_EXPORT_CONFIG,
        ExportConfigurations.class, "gzip_csv", this::createTenantLevelExportConfiguration);
    cacheObject(EXPORT_CONFIGURATION, exportConfiguration);
  }

  private ExportConfiguration createTenantLevelExportConfiguration(String name) {
    ExportConfiguration result = createObject(name, ExportConfiguration.class);
    result.setDescription("configurations");
    result.setExportType("ASYNCHRONOUS");
    result.setPipeline(cache.getObjectUri(TYPE_EXPORT_PIPELINE, "search-results-csv-gzip"));
    ExportConfiguration.Transformation transformation = new ExportConfiguration.Transformation();
    transformation.setPortName("stylesheet");
    transformation.setName(cache.getObjectUri(TYPE_EXPORT_TRANSFORMATION, "csv_xsl"));
    addTransformation(result, transformation);
    addOption(result, ExportConfiguration.DefaultOption.XSL_RESULT_FORMAT, "csv");
    return result;
  }

  private void addTransformation(ExportConfiguration exportConfiguration, Transformation transformation) {
    if (transformation != null && transformation.getPortName() != null && transformation.getName() != null) {
      exportConfiguration.getTransformations().add(transformation);
    }
  }

  private void ensureTenantLevelExportTransformations() throws IOException {
    ExportTransformation exportTransformation = ensureNamedItem(cache.getTenant(), LINK_EXPORT_TRANSFORMATION,
        ExportTransformations.class, "csv_xsl", this::createTenantLevelExportTransformation);
    cacheObject(TYPE_EXPORT_TRANSFORMATION, exportTransformation);
  }

  private ExportTransformation createTenantLevelExportTransformation(String name) {
    ExportTransformation result = createObject(name, ExportTransformation.class);
    result.setDescription("csv xsl transformation");
    result.setType("XSLT");
    result.setMainPath("search-results-csv.xsl");
    return result;
  }

  private void ensureExportPipelines() throws IOException {
    processConfiguredItems(EXPORT_PIPELINE_NAME, this::ensureExportPipeline);
  }

  private ExportPipeline ensureExportPipeline(String name) throws IOException {
    ExportPipeline exportPipeline = ensureNamedItem(cache.getApplication(), LINK_EXPORT_PIPELINE,
        ExportPipelines.class, name, this::createExportPipeline);
    return cacheObject(TYPE_EXPORT_PIPELINE, exportPipeline);
  }

  private ExportPipeline createExportPipeline(String name) {
    ExportPipeline result = createObject(name, ExportPipeline.class);
    result.setComposite(templatedBoolean(EXPORT_PIPELINE_COMPOSITE_TEMPLATE, name));
    result.setContent(templatedString(EXPORT_PIPELINE_CONTENT_TEMPLATE, name));
    result.setDescription(templatedString(EXPORT_PIPELINE_DESCRIPTION_TEMPLATE, name));
    result.setEnvelopeFormat(templatedString(EXPORT_PIPELINE_ENVELOPE_FORMAT_TEMPLATE, name));
    result.setIncludesContent(templatedBoolean(EXPORT_PIPELINE_INCLUDES_CONTENT_TEMPLATE, name));
    result.setInputFormat(templatedString(EXPORT_PIPELINE_INPUT_FORMAT_TEMPLATE, name));
    result.setOutputFormat(templatedString(EXPORT_PIPELINE_OUTPUT_FORMAT_TEMPLATE, name));
    result.setType(templatedString(EXPORT_PIPELINE_TYPE_TEMPLATE, name));
    result.setCollectionBasedExport(templatedBoolean(EXPORT_PIPELINE_COLLECTION_BASED_TEMPLATE, name));
    return result;
  }

  private String templatedString(String key, Object... name) {
    return configuration.get(resolveTemplatedKey(key, name));
  }

  private String resolveTemplatedKey(String key, Object... vars) {
    return String.format(key, vars);
  }

  private boolean templatedBoolean(String key, String name) {
    return Boolean.parseBoolean(templatedString(key, name));
  }

  private void ensureExportConfigurations() throws IOException {
    processConfiguredItems(EXPORT_CONFIG_NAME, this::ensureExportConfiguration);
  }

  private ExportConfiguration ensureExportConfiguration(String name) throws IOException {
    ExportConfiguration exportConfiguration = ensureNamedItem(cache.getApplication(), LINK_EXPORT_CONFIG,
        ExportConfigurations.class, name, this::createExportConfiguration);
    return cacheObject(EXPORT_CONFIGURATION, exportConfiguration);
  }

  private ExportConfiguration createExportConfiguration(String name) {
    ExportConfiguration result = createObject(name, ExportConfiguration.class);
    result.setExportType(templatedString(EXPORT_CONFIG_TYPE_TEMPLATE, name));
    result.setPipeline(cache.getObjectUri(TYPE_EXPORT_PIPELINE, templatedString(EXPORT_CONFIG_PIPELINE_TEMPLATE,
        name)));
    fillExportConfigurationTransformations(result);
    fillExportConfigurationOptions(result);
    fillExportConfigurationEncryptedOptions(result);
    return result;
  }

  private void fillExportConfigurationTransformations(ExportConfiguration config) {
    forEach(templatedString(EXPORT_CONFIG_TRANSFORMATIONS_TEMPLATE_NAME, config.getName()),
        name -> fillExportConfigurationTransformation(config, name));
  }

  private void forEach(String items, Consumer<String> consumer) {
    commaSeparatedItems(items).forEach(consumer);
  }

  private void fillExportConfigurationTransformation(ExportConfiguration config, String name) {
    ExportConfiguration.Transformation result = new ExportConfiguration.Transformation();
    result.setPortName(templatedString(EXPORT_CONFIG_TRANSFORMATIONS_TEMPLATE_PORTNAME_TEMPLATE,
        config.getName(), name));
    result.setName(cache.getObjectUri(TYPE_EXPORT_TRANSFORMATION, templatedString(
        EXPORT_CONFIG_TRANSFORMATIONS_TEMPLATE_TRANSFORMATION_TEMPLATE, config.getName(), name)));
    addTransformation(config, result);
  }

  private void fillExportConfigurationOptions(ExportConfiguration config) {
    addOption(config, ExportConfiguration.DefaultOption.XSL_RESULT_FORMAT,
        templatedString(EXPORT_CONFIG_OPTIONS_TEMPLATE_XSL_RESULTFORMAT_TEMPLATE, config.getName()));
    addOption(config, ExportConfiguration.DefaultOption.XQUERY_RESULT_FORMAT,
        templatedString(EXPORT_CONFIG_OPTIONS_TEMPLATE_XQUERY_RESULTFORMAT_TEMPLATE, config.getName()));
    forEach(templatedString(EXPORT_CONFIG_OPTIONS_TEMPLATE_NAME, config.getName()), name ->
        addOption(config, name, templatedString(EXPORT_CONFIG_OPTIONS_TEMPLATE_VALUE_TEMPLATE,
        config.getName(), name)));
  }

  private void addOption(ExportConfiguration exportConfiguration, DefaultOption option, String value) {
    addOption(exportConfiguration, option.getName(), value);
  }

  private void addOption(ExportConfiguration exportConfiguration, String key, String value) {
    if (key != null && value != null) {
      exportConfiguration.getOptions().put(key, value);
    }
  }

  private void fillExportConfigurationEncryptedOptions(ExportConfiguration config) {
    forEach(templatedString(EXPORT_CONFIG_ENCRYPTED_OPTIONS_TEMPLATE_NAME, config.getName()),
        name -> addEncryptedOption(config, name, templatedString(EXPORT_CONFIG_ENCRYPTED_OPTIONS_TEMPLATE_VALUE_TEMPLATE,
        config.getName(), name)));
  }

  private void addEncryptedOption(ExportConfiguration exportConfiguration, String key, String value) {
    if (key != null && value != null) {
      exportConfiguration.getEncryptedOptions().put(key, value);
    }
  }

  private void ensureExportTransformations() throws IOException {
    processConfiguredItems(EXPORT_TRANSFORMATION_NAME, this::ensureExportTransformation);
  }

  private ExportTransformation ensureExportTransformation(String name) throws IOException {
    ExportTransformation exportTransformation = ensureNamedItem(cache.getApplication(),
        LINK_EXPORT_TRANSFORMATION, ExportTransformations.class, name, this::createExportTransformation);
    return cacheObject(TYPE_EXPORT_TRANSFORMATION, exportTransformation);
  }

  private ExportTransformation createExportTransformation(String name) {
    ExportTransformation result = createObject(name, ExportTransformation.class);
    result.setDescription(templatedString(EXPORT_TRANSFORMATION_DESCRIPTION_TEMPLATE, name));
    result.setType(templatedString(EXPORT_TRANSFORMATION_TYPE_TEMPLATE, name));
    result.setMainPath(templatedString(EXPORT_TRANSFORMATION_MAIN_PATH_TEMPLATE, name));
    return result;
  }

  private void ensureIngestNode() throws IOException {
    IngestNode ingestNode = ensureNamedItem(cache.getApplication(), LINK_INGEST_NODES, IngestNodes.class,
        INGEST_NODE_NAME, this::createIngestionNode);
    cache.setIngestNodeUri(ingestNode.getSelfUri());
  }

  private IngestNode createIngestionNode(String name) {
    IngestNode result = createObject(name, IngestNode.class);
    result.setWorkingDirectory(cache.getFileSystemFolderUri());
    return result;
  }

  private void ensureRetentionPolicy() throws IOException {
    ensureItem(cache.getTenant(), LINK_RETENTION_POLICIES, RetentionPolicies.class, RETENTION_POLICY_NAME,
        this::createRetentionPolicy);
  }

  private RetentionPolicy createRetentionPolicy(String name) {
    return createObject(name, RetentionPolicy.class);
  }

  private void ensurePdi() throws IOException {
    Pdi pdi = ensureItem(cache.getApplication(), LINK_PDIS, Pdis.class, RETENTION_POLICY_NAME, this::createPdi);
    ensureContents(pdi, PDI_XML, FORMAT_XML);
    cache.setPdiUri(pdi.getSelfUri());
  }

  private Pdi createPdi(String name) {
    return createObject(name, Pdi.class);
  }

  private void ensureContents(LinkContainer state, String configurationName, String format) throws IOException {
    Contents contents = restClient.follow(state, LINK_CONTENTS, Contents.class);
    if (contents.hasItems()) {
      return;
    }
    String content = configured(configurationName);
    try (InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
      perform(() -> restClient.post(state.getUri(LINK_CONTENTS), null,
            new TextPart("content", MediaTypes.HAL, "{ \"format\": \"" + format + "\" }"),
            new BinaryPart("file", stream, configurationName)));
    }
  }

  private void ensurePdiSchema() throws IOException {
    PdiSchema pdiSchema = ensureItem(cache.getApplication(), LINK_PDI_SCHEMAS, PdiSchemas.class, PDI_SCHEMA_NAME,
        this::createPdiSchema);
    cache.setPdiSchema(pdiSchema);
    ensureContents(pdiSchema, PDI_SCHEMA, FORMAT_XSD);
  }

  private PdiSchema createPdiSchema(String name) {
    return createObject(name, PdiSchema.class);
  }

  private void ensureIngest() throws IOException {
    Ingest ingest = ensureNamedItem(cache.getApplication(), LINK_INGESTS, Ingests.class, INGEST_NAME,
        this::createIngest);
    cache.setIngestUri(ingest.getSelfUri());
    ensureContents(ingest, INGEST_XML, FORMAT_XML);
  }

  private Ingest createIngest(String name) {
    return createObject(name, Ingest.class);
  }

  private void ensureLibrary() throws IOException {
    XdbLibrary library = ensureItem(cache.getSpaceRootLibrary(), LINK_LIBRARIES, XdbLibraries.class, HOLDING_NAME,
        this::createLibrary);
    cache.setLibraryUri(library.getSelfUri());
  }

  private XdbLibrary createLibrary(String name) {
    XdbLibrary result = createObject(name, XdbLibrary.class);
    result.setSubPath("aips/" + getApplicationName().replace(' ', '-'));
    return result;
  }

  @Override
  public String getApplicationName() {
    String result = cache.getApplication().getName();
    return result == null ? configured(APPLICATION_NAME) : result;
  }

  private void ensureHolding() throws IOException {
    Holding holding = ensureItem(cache.getApplication(), LINK_HOLDINGS, Holdings.class, HOLDING_NAME,
        this::createHolding);
    cache.setHoldingUri(holding.getSelfUri());
  }

  private Holding createHolding(String name) {
    Holding result = createObject(name, Holding.class);
    setAllStores(result, cache.getStoreUri());
    IngestConfig ingestConfig = new IngestConfig();
    ingestConfig.setIngest(cache.getIngestUri());
    result.getIngestConfigs().add(ingestConfig);
    result.getIngestNodes().add(cache.getIngestNodeUri());
    SubPriority priority = new SubPriority();
    priority.setPriority(0);
    priority.setDeadline(100);
    result.getSubPriorities().add(priority);
    priority = new SubPriority();
    priority.setPriority(1);
    priority.setDeadline(200);
    result.getSubPriorities().add(priority);
    result.setXdbLibraryParent(cache.getLibraryUri());
    RetentionClass retentionClass = new RetentionClass();
    retentionClass.getPolicies().add(configured(RETENTION_POLICY_NAME));
    result.getRetentionClasses().add(retentionClass);
    PdiConfig pdiConfig = new PdiConfig();
    pdiConfig.setPdi(cache.getPdiUri());
    pdiConfig.setSchema(configured(PDI_SCHEMA_NAME));
    result.getPdiConfigs().add(pdiConfig);
    return result;
  }

  private void setAllStores(Holding holding, String store) {
    holding.setCiStore(store);
    holding.setLogStore(store);
    holding.setRenditionStore(store);
    holding.setSipStore(store);
    holding.setXdbStore(store);
    holding.setXmlStore(store);
    holding.setManagedItemStore(store);
  }

  private void ensureAic() throws IOException {
    Aic aic = ensureItem(cache.getApplication(), LINK_AICS, Aics.class, AIC_NAME, this::createAic);
    cache.setAicUri(aic.getSelfUri());
    cache.setAicUriByName(aic.getName(), aic.getSelfUri());
  }

  private Aic createAic(String name) {
    Aic result = createObject(name, Aic.class);
    result.setCriterias(createCriteria());
    result.getHoldings().add(cache.getHoldingUri());
    return result;
  }

  private List<Criterion> createCriteria() {
    List<Criterion> result = new ArrayList<>();
    RepeatingConfigReader reader = new RepeatingConfigReader("criteria", Arrays.asList(CRITERIA_NAME, CRITERIA_LABEL,
        CRITERIA_TYPE, CRITERIA_PKEYMINATTR, CRITERIA_PKEYMAXATTR, CRITERIA_PKEYVALUESATTR, CRITERIA_INDEXED));
    for (Map<String, String> cfg : reader.read(configuration)) {
      result.add(createCriterion(cfg));
    }
    return result;
  }

  private Criterion createCriterion(Map<String, String> config) {
    Criterion criterion = new Criterion();
    criterion.setIndexed(Boolean.parseBoolean(config.get(CRITERIA_INDEXED)));
    criterion.setLabel(config.get(CRITERIA_LABEL));
    criterion.setName(config.get(CRITERIA_NAME));

    String minAttr = config.get(CRITERIA_PKEYMINATTR).trim();
    if (!minAttr.isEmpty()) {
      criterion.setpKeyMinAttr(minAttr);
    }

    String maxAttr = config.get(CRITERIA_PKEYMAXATTR).trim();
    if (!maxAttr.isEmpty()) {
      criterion.setpKeyMaxAttr(maxAttr);
    }

    String valueAttr = config.get(CRITERIA_PKEYVALUESATTR).trim();
    if (!valueAttr.isEmpty()) {
      criterion.setpKeyValuesAttr(valueAttr);
    }
    criterion.setType(config.get(CRITERIA_TYPE));
    return criterion;
  }

  private void ensureQueries() throws IOException {
    List<String> queryUris = new ArrayList<>();
    processConfiguredItems(QUERY_NAME, name -> queryUris.add(ensureQuery(name).getSelfUri()));
    cache.setQueryUris(queryUris);
  }

  private Query ensureQuery(String name) throws IOException {
    Query result = ensureNamedItem(cache.getApplication(), LINK_QUERIES, Queries.class, name, this::createQuery);
    cache.setQueryUriByName(name, result.getSelfUri());
    return result;
  }

  private Query createQuery(String name) {
    Query result = createObject(name, Query.class);

    result.setResultRootElement(templatedString(QUERY_RESULT_ROOT_ELEMENT_TEMPLATE, name));
    result.setResultRootNsEnabled(
        Boolean.parseBoolean(templatedString(QUERY_RESULT_ROOT_NS_ENABLED_TEMPLATE, name)));
    result.setResultSchema(templatedString(QUERY_RESULT_SCHEMA_TEMPLATE, name));

    result.setNamespaces(createNamespaces(name));
    result.setXdbPdiConfigs(createXdbPdiConfigs(name));

    result.setQuota(cache.getQuotaUri());
    result.setQuotaAsync(cache.getQuotaUri());
    result.getAics().add(cache.getAicUri());

    return result;
  }

  private List<Namespace> createNamespaces(String name) {
    RepeatingConfigReader reader = new RepeatingConfigReader("namespace", resolveTemplatedKeys(Arrays.asList(
        QUERY_NAMESPACE_PREFIX_TEMPLATE, QUERY_NAMESPACE_URI_TEMPLATE), name));
    List<Map<String, String>> namespaceCfgs = reader.read(configuration);
    List<Namespace> result = new ArrayList<>();
    for (Map<String, String> cfg : namespaceCfgs) {
      Namespace namespace = new Namespace();
      namespace.setPrefix(cfg.get(resolveTemplatedKey(QUERY_NAMESPACE_PREFIX_TEMPLATE, name)));
      namespace.setUri(cfg.get(resolveTemplatedKey(QUERY_NAMESPACE_URI_TEMPLATE, name)));
      result.add(namespace);
    }
    return result;
  }

  private List<String> resolveTemplatedKeys(List<String> templatedKeys, Object... vars) {
    return templatedKeys.stream()
      .map(key -> resolveTemplatedKey(key, vars))
      .collect(Collectors.toList());
  }

  private List<XdbPdiConfig> createXdbPdiConfigs(String name) {
    List<XdbPdiConfig> result = new ArrayList<>();
    for (Map<String, String> cfg : getPdiConfigs(name)) {
      XdbPdiConfig xdbPdi = new XdbPdiConfig();
      xdbPdi.setEntityPath(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, name)));
      xdbPdi.setSchema(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_SCHEMA_TEMPLATE, name)));
      xdbPdi.setTemplate(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_TEMPLATE_TEMPLATE, name)));
      xdbPdi.setOperands(createOperands(name, xdbPdi.getSchema()));
      result.add(xdbPdi);
    }
    return result;
  }

  private List<Map<String, String>> getPdiConfigs(String name) {
    return new RepeatingConfigReader("xdbpdiconfigs", resolveTemplatedKeys(
        Arrays.asList(QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, QUERY_XDBPDI_SCHEMA_TEMPLATE, QUERY_XDBPDI_TEMPLATE_TEMPLATE),
        name)).read(configuration);
  }

  private List<Operand> createOperands(String name, String schema) {
    List<Operand> result = new ArrayList<>();
    for (Map<String, String> cfg : getOperandConfigs(name, schema)) {
      Operand operand = new Operand();
      operand.setIndex(Boolean.parseBoolean(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_INDEX, name, schema))));
      operand.setType(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_TYPE, name, schema)));
      operand.setName(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_NAME, name, schema)));
      operand.setPath(cfg.get(resolveTemplatedKey(QUERY_XDBPDI_OPERAND_PATH, name, schema)));
      result.add(operand);
    }
    return result;
  }

  private List<Map<String, String>> getOperandConfigs(String name, String schema) {
    return new RepeatingConfigReader("operands", resolveTemplatedKeys(Arrays.asList(
        QUERY_XDBPDI_OPERAND_NAME, QUERY_XDBPDI_OPERAND_PATH, QUERY_XDBPDI_OPERAND_TYPE, QUERY_XDBPDI_OPERAND_INDEX),
        name, schema)).read(configuration);
  }

  private void ensureQuota() throws IOException {
    QueryQuota quota = ensureItem(cache.getApplication(), LINK_QUERY_QUOTAS, QueryQuotas.class, QUOTA_NAME, this::createQuota);
    cache.setQuotaUri(quota.getSelfUri());

  }

  private QueryQuota createQuota(String name) {
    QueryQuota result = createObject(name, QueryQuota.class);
    result.setAipQuota(getOptionalInt(QUOTA_AIP, 0));
    result.setAiuQuota(getOptionalInt(QUOTA_AIU, 0));
    result.setDipQuota(getOptionalInt(QUOTA_DIP, 0));
    return result;
  }

  private int getOptionalInt(String name, int defaultValue) {
    String value = configuration.get(name);
    return isBlank(value) ? defaultValue : Integer.parseInt(value);
  }

  private void ensureResultConfigurationHelper() throws IOException {
    String name = configuration.getOrDefault(RESULT_HELPER_NAME, DEFAULT_RESULT_HELPER_NAME);
    String helperXmlName = resolveTemplatedKey(RESULT_HELPER_XML, name);
    if (configuration.containsKey(helperXmlName)) {
      ResultConfigurationHelper helper = ensureNamedItem(cache.getApplication(), LINK_RESULT_CONFIGURATION_HELPERS,
          ResultConfigurationHelpers.class, name, this::createResultConfigurationHelper);
      ensureContents(helper, helperXmlName, FORMAT_XML);
      cache.setResultConfigHelperUri(helper.getSelfUri());
    }
  }

  private ResultConfigurationHelper createResultConfigurationHelper(String name) {
    ResultConfigurationHelper result = createObject(name, ResultConfigurationHelper.class);
    result.setResultSchema(createResultSchema(name));
    return result;
  }

  private List<String> createResultSchema(String name) {
    return Collections.singletonList(templatedString(RESULT_HELPER_SCHEMA_TEMPLATE, name));
  }

  private void ensureSearches() throws IOException {
    processConfiguredItems(SEARCH_NAME, this::ensureSearch);
  }

  private Search ensureSearch(String name) throws IOException {
    Search result = ensureNamedItem(cache.getApplication(), LINK_SEARCHES, Searches.class, name, this::createSearch);
    cache.setSearch(result);
    ensureSearchComposition(name);
    return result;
  }

  private Search createSearch(String name) {
    Search result = createObject(name, Search.class);
    result.setDescription(templatedString(SEARCH_DESCRIPTION, name));
    result.setNestedSearch(Boolean.parseBoolean(templatedString(SEARCH_NESTED, name)));
    result.setState(templatedString(SEARCH_STATE, name));
    result.setInUse(Boolean.parseBoolean(templatedString(SEARCH_INUSE, name)));
    result.setAic(cache.getAicUriByName(templatedString(SEARCH_AIC, name)));
    result.setQuery(cache.getQueryUriByName(templatedString(SEARCH_QUERY, name)));
    return result;
  }

  private void ensureSearchComposition(String searchName) throws IOException {
    String name = templatedString(SEARCH_COMPOSITION_NAME, searchName);
    if (isBlank(name)) {
      name = "Set 1";
    }
    SearchComposition composition = ensureNamedItem(cache.getSearch(), LINK_SEARCH_COMPOSITIONS,
        SearchCompositions.class, name, this::createSearchComposition);
    ensureAllSearchComponents(searchName, composition);
    cache.setSearchComposition(composition);
  }

  private SearchComposition createSearchComposition(String name) {
    SearchComposition result = createObject(name, SearchComposition.class);
    result.setSearchName(SEARCH_NAME);
    return result;
  }

  private void ensureAllSearchComponents(String searchName, SearchComposition composition) throws IOException {
    AllSearchComponents components = new AllSearchComponents();
    components.setSearchComposition(composition);
    components.setXform(createXForm(searchName, composition.getName(), composition.getVersion()));
    components.setResultMaster(createResultMaster(searchName, composition.getName(), composition.getVersion()));
    SearchComposition updatedComposition = restClient.put(composition.getUri(LINK_ALLCOMPONENTS),
        SearchComposition.class, components);
    Objects.requireNonNull(updatedComposition, "Failed to update search composition");
  }

  private XForm createXForm(String searchName, String compositionName, long version) {
    XForm result = new XForm();
    result.setForm(configured(resolveTemplatedKey(SEARCH_COMPOSITION_XFORM, searchName, compositionName)));
    result.setVersion(version);
    return result;
  }

  private ResultMaster createResultMaster(String searchName, String compositionName, long version) {
    ResultMaster result = new ResultMaster();
    result.setVersion(version);

    result.setNamespaces(createNamespaces(templatedString(SEARCH_QUERY, searchName)));
    List<Column> columns = result.getDefaultTab().getColumns();

    result.getDefaultTab().setExportEnabled(templatedBoolean(SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_ENABLED_TEMPLATE,
        searchName));
    result.getDefaultTab().getExportConfigurations().addAll(uriFromNamesAndType(EXPORT_CONFIGURATION, getStrings(
          SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_CONFIG_TEMPLATE, searchName)));

    for (Map<String, String> config : getSearchColumnConfigs(searchName, compositionName)) {
      String name = config.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME, searchName,
          compositionName));
      String label = config.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL, searchName,
          compositionName));
      String path = config.get(resolveTemplatedKey(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH, searchName,
          compositionName));
      DataType dataType = DataType.valueOf(config.get(resolveTemplatedKey(
          SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE, searchName, compositionName)));
      DefaultSort sortOrder = DefaultSort.valueOf(config.get(resolveTemplatedKey(
          SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT, searchName, compositionName)));

      Column column = Column.fromSchema(name, label, path, dataType, sortOrder);
      columns.add(column);
    }
    return result;
  }

  private Collection<String> getStrings(String key, String variable) {
    return commaSeparatedItems(templatedString(key, variable));
  }

  private Collection<String> uriFromNamesAndType(String type, Collection<String> names) {
    return names.stream()
      .map(name -> cache.getObjectUri(type, name))
      .collect(Collectors.toList());
  }

  private List<Map<String, String>> getSearchColumnConfigs(String searchName, String compositionName) {
    return new RepeatingConfigReader("maincolumns", resolveTemplatedKeys(Arrays.asList(
        SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME, SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL,
        SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH, SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE,
        SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT), searchName, compositionName)).read(configuration);
  }

  private void ensureCryptoObject() throws IOException {
    ensureOptionalItem(cache.getServices(), LINK_CRYPTO_OBJECTS, CryptoObjects.class, CRYPTO_OBJECT_NAME,
        this::createCryptoObject).ifPresent(cache::setCryptoObject);
  }

  private <T extends NamedLinkContainer> Optional<T> ensureOptionalItem(LinkContainer collectionOwner,
      String collectionLinkRelation, Class<? extends ItemContainer<T>> collectionType, String objectName,
          Function<String, ? extends T> objectCreator) throws IOException {
    return Optional.ofNullable(
        ensureItem(collectionOwner, collectionLinkRelation, collectionType, objectName, objectCreator, true));
  }

  private CryptoObject createCryptoObject(String name) {
    CryptoObject result = createObject(name, CryptoObject.class);
    result.setSecurityProvider(configuration.get(CRYPTO_OBJECT_SECURITY_PROVIDER));
    result.setKeySize(Integer.parseInt(configuration.get(CRYPTO_OBJECT_KEY_SIZE)));
    result.setInUse(configuredBoolean(CRYPTO_OBJECT_IN_USE));
    result.setEncryptionMode(configuration.get(CRYPTO_OBJECT_ENCRYPTION_MODE));
    result.setPaddingScheme(configuration.get(CRYPTO_OBJECT_PADDING_SCHEME));
    result.setEncryptionAlgorithm(configuration.get(CRYPTO_OBJECT_ENCRYPTION_ALGORITHM));
    return result;
  }

  private boolean configuredBoolean(String key) {
    return Boolean.parseBoolean(configuration.getOrDefault(key, Boolean.TRUE.toString()));
  }

  private void ensurePdiCrypto() throws IOException {
    ensureOptionalItem(cache.getApplication(), LINK_PDI_CRYPTOS, PdiCryptoes.class, PDI_CRYPTO_NAME,
        this::createPdiCrypto).ifPresent(pdiCrypto ->
      cache.setPdiCryptoUri(pdiCrypto.getSelfUri()));
  }

  private PdiCrypto createPdiCrypto(String name) {
    PdiCrypto result = createObject(name, PdiCrypto.class);
    result.setApplication(cache.getApplication().getSelfUri());
    return result;
  }

  private void ensureHoldingCrypto() throws IOException {
    ensureOptionalItem(cache.getApplication(), LINK_HOLDING_CRYPTOS, HoldingCryptoes.class, HOLDING_CRYPTO_NAME,
        this::createHoldingCrypto);
  }

  private HoldingCrypto createHoldingCrypto(String name) {
    HoldingCrypto result = createObject(name, HoldingCrypto.class);
    result.setApplication(cache.getApplication().getSelfUri());
    result.setHolding(cache.getHoldingUri());
    HoldingCrypto.PdiCryptoConfig pdiCryptoConfig = new HoldingCrypto.PdiCryptoConfig();
    pdiCryptoConfig.setSchema(cache.getPdiSchema().getName());
    pdiCryptoConfig.setPdiCrypto(cache.getPdiCryptoUri());
    result.setPdis(Collections.singletonList(pdiCryptoConfig));
    result.setCryptoEncoding(configuration.getOrDefault(HOLDING_CRYPTO_ENCODING, "base64"));
    HoldingCrypto.ObjectCryptoConfig objectCryptoConfig = new HoldingCrypto.ObjectCryptoConfig();
    objectCryptoConfig.setCryptoEnabled(configuredBoolean(HOLDING_CRYPTO_ENABLED));
    objectCryptoConfig.setCryptoObject(cache.getCryptoObject().getSelfUri());
    result.setSip(objectCryptoConfig);
    result.setPdi(objectCryptoConfig);
    result.setCi(objectCryptoConfig);
    return result;
  }

  private void ensureStorageEndPoint() throws IOException {
    ensureOptionalItem(cache.getServices(), LINK_STORAGE_END_POINTS, StorageEndPoints.class, STORAGE_END_POINT_NAME,
        this::createStorageEndPoint);
  }

  private StorageEndPoint createStorageEndPoint(String name) {
    StorageEndPoint result = createObject(name, StorageEndPoint.class);
    result.setType(configuration.get(STORAGE_END_POINT_TYPE));
    result.setDescription(configuration.get(STORAGE_END_POINT_DESCRIPTION));
    result.setUrl(configuration.get(STORAGE_END_POINT_URL));
    result.setProxyUrl(configuration.get(STORAGE_END_POINT_PROXY_URL));
    return result;
  }

  private void ensureCustomStorage() throws IOException {
    ensureOptionalItem(cache.getServices(), LINK_CUSTOM_STORAGES, CustomStorages.class, CUSTOM_STORAGE_NAME,
        this::createCustomStorage);
  }

  private CustomStorage createCustomStorage(String name) {
    CustomStorage result = createObject(name, CustomStorage.class);
    result.setDescription(configuration.get(CUSTOM_STORAGE_DESCRIPTION));
    result.setFactoryServiceName(configuration.get(CUSTOM_STORAGE_FACTORY_SERVICE_NAME));
    Map<String, String> properties = new HashMap<>();
    properties.put(configuration.get(CUSTOM_STORAGE_PROPERTY_NAME), configuration.get(CUSTOM_STORAGE_PROPERTY_VALUE));
    result.setProperties(properties);
    return result;
  }

  private void ensureContentAddressedStorage() throws IOException {
    ensureOptionalItem(cache.getServices(), LINK_CONTENT_ADDRESSED_STORAGES, ContentAddressedStorages.class,
        CONTENT_ADDRESSED_STORAGE_NAME, this::createContentAddressedStorage);
  }

  private ContentAddressedStorage createContentAddressedStorage(String name) {
    ContentAddressedStorage result = createObject(name, ContentAddressedStorage.class);
    result.setConnexionString(configuration.get(CONTENT_ADDRESSED_STORAGE_CONNEXION_STRING));
    Map<String, String> peas = new HashMap<>();
    peas.put(configuration.get(CONTENT_ADDRESSED_STORAGE_PEA_NAME), configuration.get(CONTENT_ADDRESSED_STORAGE_PEA_VALUE));
    result.setPeas(peas);
    return result;
  }


  @FunctionalInterface
  interface Operation<T> {
    T perform() throws IOException;
  }


  @FunctionalInterface
  private interface Processor {
    Object accept(String name) throws IOException;
  }

}
