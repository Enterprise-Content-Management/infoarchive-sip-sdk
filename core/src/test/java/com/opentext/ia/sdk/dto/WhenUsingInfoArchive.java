/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.OngoingStubbing;

import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.api.ContentResult;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.client.factory.ArchiveClients;
import com.opentext.ia.sdk.client.impl.ContentResultFactory;
import com.opentext.ia.sdk.client.impl.DefaultContentResult;
import com.opentext.ia.sdk.dto.ProductInfo.BuildProperties;
import com.opentext.ia.sdk.dto.export.ExportConfiguration;
import com.opentext.ia.sdk.dto.export.ExportConfigurations;
import com.opentext.ia.sdk.dto.export.ExportPipeline;
import com.opentext.ia.sdk.dto.export.ExportPipelines;
import com.opentext.ia.sdk.dto.export.ExportTransformation;
import com.opentext.ia.sdk.dto.export.ExportTransformations;
import com.opentext.ia.sdk.dto.query.Comparison;
import com.opentext.ia.sdk.dto.query.Item;
import com.opentext.ia.sdk.dto.query.Operator;
import com.opentext.ia.sdk.dto.query.SearchQuery;
import com.opentext.ia.sdk.dto.result.AllSearchComponents;
import com.opentext.ia.sdk.server.configuration.PropertiesBasedArchiveConnection;
import com.opentext.ia.sdk.support.http.HttpException;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.Part;
import com.opentext.ia.sdk.support.http.Response;
import com.opentext.ia.sdk.support.http.ResponseFactory;
import com.opentext.ia.sdk.support.http.UriBuilder;
import com.opentext.ia.sdk.support.http.rest.Link;
import com.opentext.ia.sdk.support.http.rest.LinkContainer;
import com.opentext.ia.sdk.support.http.rest.RestClient;
import com.opentext.ia.sdk.support.io.RuntimeIoException;
import com.opentext.ia.test.TestCase;

@SuppressWarnings({ "PMD", "deprecation" }) // TODO: Clean this up
public class WhenUsingInfoArchive extends TestCase implements InfoArchiveLinkRelations {

  private static final String BILLBOARD_URI = "http://foo.com/bar";
  private static final String AUTH_TOKEN = "XYZ123ABC";
  private static final String APPLICATION_NAME = "ApPlIcAtIoN";
  private static final String TENANT_NAME = "TeNaNt";
  private static final String NAMESPACE = "urn:SoMeNaMeSpAcE";
  private static final String SOURCE = "This is the source of my input stream";

  @Rule
  public final ExpectedException thrown = ExpectedException.none();
  private final Map<String, Link> links = new HashMap<>();
  private final Map<String, String> configuration = new HashMap<>();
  private final RestClient restClient = mock(RestClient.class);
  private ArchiveConnection connection;
  private ArchiveClient archiveClient;
  private Applications applications;
  private Application application;
  private XdbFederations federations;

  @Before
  public void init() throws IOException {
    prepareConfiguration();
    connection = new PropertiesBasedArchiveConnection(configuration);
    connection.setRestClient(restClient);

    Services resource = new Services();
    Link link = mock(Link.class);
    Tenant tenant = new Tenant();
    application = new Application();
    applications = mock(Applications.class);
    federations = mock(XdbFederations.class);
    Spaces spaces = mock(Spaces.class);
    XdbDatabases databases = mock(XdbDatabases.class);
    FileSystemRoots fileSystemRoots = mock(FileSystemRoots.class);
    FileSystemRoot fileSystemRoot = new FileSystemRoot();
    when(fileSystemRoots.first()).thenReturn(fileSystemRoot);
    Holdings holdings = mock(Holdings.class);
    ReceiverNodes receiverNodes = mock(ReceiverNodes.class);
    SpaceRootXdbLibraries spaceRootLibraries = mock(SpaceRootXdbLibraries.class);
    SpaceRootFolders rootFolders = mock(SpaceRootFolders.class);
    FileSystemFolders systemFolders = mock(FileSystemFolders.class);
    Stores stores = mock(Stores.class);
    IngestNodes ingestionNodes = mock(IngestNodes.class);
    RetentionPolicies retentionPolicies = mock(RetentionPolicies.class);
    Pdis pdis = mock(Pdis.class);
    PdiSchemas pdiSchemas = mock(PdiSchemas.class);
    Ingests ingests = mock(Ingests.class);
    XdbLibraries libraries = mock(XdbLibraries.class);
    Contents contents = new Contents();
    Aics aics = mock(Aics.class);
    LinkContainer aips = new LinkContainer();
    Queries queries = mock(Queries.class);
    QueryQuotas quotas = mock(QueryQuotas.class);
    ResultConfigurationHelpers helpers = mock(ResultConfigurationHelpers.class);
    ExportConfigurations exportConfigurations = mock(ExportConfigurations.class);
    ExportPipelines exportPipelines = mock(ExportPipelines.class);
    ExportTransformations exportTransformations = mock(ExportTransformations.class);
    Searches searches = mock(Searches.class);
    SearchCompositions compositions = mock(SearchCompositions.class);
    XForms xForms = mock(XForms.class);
    XForm xForm = mock(XForm.class);
    CryptoObjects cryptoObjects = mock(CryptoObjects.class);
    PdiCryptoes pdiCryptos = mock(PdiCryptoes.class);
    HoldingCryptoes holdingCryptos = mock(HoldingCryptoes.class);
    StorageEndPoints storageEndPoints = mock(StorageEndPoints.class);
    CustomStorages customStorages = mock(CustomStorages.class);
    ContentAddressedStorages contentAddressedStorages = mock(ContentAddressedStorages.class);

    Aic aic = new Aic();

    links.put(InfoArchiveLinkRelations.LINK_TENANT, link);
    links.put(InfoArchiveLinkRelations.LINK_APPLICATIONS, link);
    links.put(InfoArchiveLinkRelations.LINK_AIPS, link);
    links.put(InfoArchiveLinkRelations.LINK_INGEST, link);
    resource.setLinks(links);
    tenant.setLinks(links);
    application.setLinks(links);

    contents.getLinks().put(LINK_DOWNLOAD, new Link());
    when(restClient.follow(any(LinkContainer.class), eq(LINK_CONTENTS), eq(Contents.class)))
        .thenReturn(contents);

    when(restClient.get(BILLBOARD_URI, Services.class)).thenReturn(resource);
    when(restClient.follow(resource, LINK_TENANT, Tenant.class)).thenReturn(tenant);
    ProductInfo productInfo = new ProductInfo();
    BuildProperties buildProperties = new BuildProperties();
    buildProperties.setServerVersion("16.3");
    productInfo.setBuildProperties(buildProperties);
    when(restClient.follow(resource, LINK_PRODUCT_INFO, ProductInfo.class)).thenReturn(productInfo);
    when(link.getHref()).thenReturn(BILLBOARD_URI);
    when(restClient.follow(application, InfoArchiveLinkRelations.LINK_AIPS, LinkContainer.class))
        .thenReturn(aips);
    aips.setLinks(links);

    mockCollection(Applications.class, applications);
    mockCollection(XdbFederations.class, federations);
    mockCollection(Spaces.class, spaces);
    mockCollection(XdbDatabases.class, databases);
    mockCollection(FileSystemRoots.class, fileSystemRoots);
    mockCollection(Holdings.class, holdings);
    mockCollection(ReceiverNodes.class, receiverNodes);
    mockCollection(SpaceRootXdbLibraries.class, spaceRootLibraries);
    mockCollection(SpaceRootFolders.class, rootFolders);
    mockCollection(FileSystemFolders.class, systemFolders);
    mockCollection(Stores.class, stores);
    mockCollection(IngestNodes.class, ingestionNodes);
    mockCollection(RetentionPolicies.class, retentionPolicies);
    mockCollection(Pdis.class, pdis);
    mockCollection(PdiSchemas.class, pdiSchemas);
    mockCollection(Ingests.class, ingests);
    mockCollection(XdbLibraries.class, libraries);
    mockCollection(Aics.class, aics);
    mockCollection(QueryQuotas.class, quotas);
    mockCollection(Queries.class, queries);
    mockCollection(ResultConfigurationHelpers.class, helpers);
    mockCollection(ExportConfigurations.class, exportConfigurations);
    mockCollection(ExportPipelines.class, exportPipelines);
    mockCollection(ExportTransformations.class, exportTransformations);
    mockCollection(Searches.class, searches);
    mockCollection(SearchCompositions.class, compositions);
    mockCollection(XForms.class, xForms);
    mockCollection(CryptoObjects.class, cryptoObjects);
    mockCollection(PdiCryptoes.class, pdiCryptos);
    mockCollection(HoldingCryptoes.class, holdingCryptos);
    mockCollection(StorageEndPoints.class, storageEndPoints);
    mockCollection(CustomStorages.class, customStorages);
    mockCollection(ContentAddressedStorages.class, contentAddressedStorages);
    when(restClient.createCollectionItem(any(LinkContainer.class), any(XForm.class), eq(LINK_SELF)))
        .thenReturn(xForm);

    mockByName(federations, new XdbFederation());
    mockByName(databases, new XdbDatabase());
    mockByName(applications, application);
    mockByName(spaces, new Space());
    mockByName(spaceRootLibraries, new SpaceRootXdbLibrary());
    mockByName(rootFolders, new SpaceRootFolder());
    mockByName(fileSystemRoots, new FileSystemRoot());
    mockByName(systemFolders, new FileSystemFolder());
    mockByName(stores, new Store());
    mockByName(receiverNodes, new ReceiverNode());
    mockByName(ingestionNodes, new IngestNode());
    mockByName(retentionPolicies, new RetentionPolicy());
    mockByName(pdis, new Pdi());
    mockByName(pdiSchemas, new PdiSchema());
    mockByName(ingests, new Ingest());
    mockByName(libraries, new XdbLibrary());
    mockByName(holdings, new Holding());
    mockByName(aics, aic);
    mockByName(quotas, new QueryQuota());
    mockByName(queries, new Query());
    mockByName(helpers, new ResultConfigurationHelper());
    mockByName(exportConfigurations, new ExportConfiguration());
    mockByName(exportPipelines, new ExportPipeline());
    mockByName(exportTransformations, new ExportTransformation());
    mockByName(searches, new Search());
    mockByName(compositions, new SearchComposition());
    mockByName(xForms, new XForm());
    mockByName(cryptoObjects, new CryptoObject());
    mockByName(pdiCryptos, new PdiCrypto());
    mockByName(holdingCryptos, new HoldingCrypto());
    mockByName(storageEndPoints, new StorageEndPoint());
    mockByName(customStorages, new CustomStorage());
    mockByName(contentAddressedStorages, new ContentAddressedStorage());

    when(restClient.put(any(), eq(SearchComposition.class), any(AllSearchComponents.class)))
        .thenReturn(new SearchComposition());

    when(aics.getItems()).thenReturn(Stream.of(aic));
  }

  private void prepareConfiguration() {
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SERVER_URI,
        BILLBOARD_URI);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SERVER_AUTHENTICATION_TOKEN,
        AUTH_TOKEN);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.TENANT_NAME,
        TENANT_NAME);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.DATABASE_NAME,
        APPLICATION_NAME);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.DATABASE_ADMIN_PASSWORD,
        APPLICATION_NAME);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.APPLICATION_NAME,
        APPLICATION_NAME);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.HOLDING_NAME,
        APPLICATION_NAME);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.RETENTION_POLICY_NAME,
        APPLICATION_NAME);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.PDI_XML,
        "");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.PDI_SCHEMA_NAME,
        APPLICATION_NAME);
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.PDI_SCHEMA,
        "");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.INGEST_XML,
        "");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_DESCRIPTION,
        "Default emails search");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_NESTED,
        Boolean.FALSE.toString());
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_NAME,
        "emailsSearch");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_STATE,
        "DRAFT");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_COMPOSITION_NAME,
        "DefaultSearchComposition");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_COMPOSITION_XFORM_NAME,
        "Test Search Form");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_DEFAULT_RESULT_MASTER,
        "");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.SEARCH_DEFAULT_SEARCH,
        "");

    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.PROXY_HOST,
        "localhost");
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.PROXY_PORT,
        "8080");

    configuration.put("ia.aic.name", "MyAic");
    configuration.put("ia.aic.criteria.name", "name");
    configuration.put("ia.aic.criteria.label", "name");
    configuration.put("ia.aic.criteria.type", "STRING");
    configuration.put("ia.aic.criteria.pkeyminattr", "");
    configuration.put("ia.aic.criteria.pkeymaxattr", "");
    configuration.put("ia.aic.criteria.pkeyvaluesattr", "");
    configuration.put("ia.aic.criteria.indexed", Boolean.TRUE.toString());

    configuration.put("ia.query.name", "Query");
    configuration.put("ia.query.Query.namespace.prefix", "n");
    configuration.put("ia.query.Query.namespace.uri", NAMESPACE);
    configuration.put("ia.query.Query.result.root.element", "messages");
    configuration.put("ia.query.Query.result.root.ns.enabled", Boolean.TRUE.toString());
    configuration.put("ia.query.Query.result.schema", NAMESPACE);

    configuration.put("ia.query.Query.xdbpdi.entity.path", "/n:object/n:objects");
    configuration.put("ia.query.Query.xdbpdi.schema", NAMESPACE);
    configuration.put("ia.query.Query.xdbpdi.template", "return $aiu");

    String queryPrefix = "ia.query.Query.xdbpdi[";
    configuration.put(queryPrefix + NAMESPACE + "].operand.name", "name");
    configuration.put(queryPrefix + NAMESPACE + "].operand.path", "n:name");
    configuration.put(queryPrefix + NAMESPACE + "].operand.type", "STRING");
    configuration.put(queryPrefix + NAMESPACE + "].operand.index", Boolean.TRUE.toString());

    configuration.put("ia.quota.name", "Quota");
    configuration.put("ia.quota.aiu", "0");
    configuration.put("ia.quota.aip", "0");
    configuration.put("ia.quota.dip", "0");

    configuration.put("ia.result.helper.result_helper.xml", "<somexml></somexml>");
    configuration.put("ia.search.emailsSearch.composition.xform", "<form></form>");
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.name", "sender");
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.label", "Sender");
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.path",
        "n:sender/n:email");
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.type", "STRING");
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.sort", "NONE");
    configuration.put("ia.search.emailsSearch.composition.exportconfigs",
        "exportConfig1,exportconfig2");

    prepareExport();
    prepareCrypto();
    prepareStorages();
  }

  private void prepareExport() {
    configuration.put("ia.exportpipeline.names", "ExportPipeline");
    configuration.put("ia.exportpipeline.ExportPipeline.composite", "NONE");
    configuration.put("ia.exportpipeline.ExportPipeline.content", "<pipeline></pipeline>");
    configuration.put("ia.exportpipeline.ExportPipeline.description",
        "gzip envelope for xsl csv export");
    configuration.put("ia.exportpipeline.ExportPipeline.envelopeformat", "gzip");
    configuration.put("ia.exportpipeline.ExportPipeline.includescontent", Boolean.TRUE.toString());
    configuration.put("ia.exportpipeline.ExportPipeline.outputformat", "csv");
    configuration.put("ia.exportpipeline.ExportPipeline.inputformat", "ROW_COLUMN");
    configuration.put("ia.exportpipeline.ExportPipeline.type", "NONE");
    configuration.put("ia.exportpipeline.ExportPipeline.collectionbased", Boolean.FALSE.toString());

    configuration.put("ia.exportconfig.names", "ExportConfiguration");
    configuration.put("ia.exportconfig.ExportConfiguration.type", "ASYNCHRONOUS");
    configuration.put("ia.exportconfig.ExportConfiguration.pipeline", "ExportPipeline");
    configuration.put("ia.exportconfig.ExportConfiguration.transformations.names", "CsvXsl");
    configuration.put("ia.exportconfig.ExportConfiguration.transformations.CsvXsl.portname",
        "stylesheet");
    configuration.put("ia.exportconfig.ExportConfiguration.transformations.CsvXsl.transformation",
        "ExportTransformation");
    configuration.put("ia.exportconfig.ExportConfiguration.options.xslresultformat.value", "csv");
    configuration.put("ia.exportconfig.ExportConfiguration.options.names", "FtpHost,FtpPort");
    configuration.put("ia.exportconfig.ExportConfiguration.options.FtpHost.value", "localhost");
    configuration.put("ia.exportconfig.ExportConfiguration.options.FtpPort.value", "21");
    configuration.put("ia.exportconfig.ExportConfiguration.encryptedoptions.names",
        "FtpLogin,FtpPass");
    configuration.put("ia.exportconfig.ExportConfiguration.encryptedoptions.FtpLogin.value",
        "login");
    configuration.put("ia.exportconfig.ExportConfiguration.encryptedoptions.FtpPass.value",
        "password");

    configuration.put("ia.exporttransformation.names", "ExportTransformation");
    configuration.put("ia.exporttransformation.ExportTransformation.description",
        "csv xsl transformation");
    configuration.put("ia.exporttransformation.ExportTransformation.type", "XSLT");
    configuration.put("ia.exporttransformation.ExportTransformation.mainpath",
        "search-results-csv.xsl");
  }

  private void prepareCrypto() {
    configuration.put("ia.crypto.object.name", "MyCryptoObject");
    configuration.put("ia.crypto.object.security.provider", "Bouncy Castle");
    configuration.put("ia.crypto.object.key.size", "256");
    configuration.put("ia.crypto.object.in.use", Boolean.TRUE.toString());
    configuration.put("ia.crypto.object.encryption.mode", "CBC");
    configuration.put("ia.crypto.object.padding.scheme", "PKCS7PADDING");
    configuration.put("ia.crypto.object.encryption.algorithm", "AES");

    configuration.put("ia.pdi.crypto.name", "MyPdiCrypto");

    configuration.put("ia.holding.crypto.name", "MyHoldingCrypto");
    configuration.put("ia.holding.crypto.encoding", "base64");
    configuration.put("ia.holding.crypto.enabled", Boolean.TRUE.toString());
  }

  private void prepareStorages() {
    configuration.put("ia.storage.end.point.name", "MyStorageEndPoint");
    configuration.put("ia.storage.end.point.type", "ECS");
    configuration.put("ia.storage.end.point.description", "MyStorageEndPointDescription");
    configuration.put("ia.storage.end.point.url", "http://localhost");
    configuration.put("ia.storage.end.point.proxy.url", "http://localhost");

    configuration.put("ia.custom.storage.name", "MyCustomStorage");
    configuration.put("ia.custom.storage.description", "MyCustomStorageDescription");
    configuration.put("ia.custom.storage.factory.service.name",
        "MyCustomStorageFactoryServiceName");
    configuration.put("ia.custom.storage.property.name", "MyCustomStoragePropertyName");
    configuration.put("ia.custom.storage.property.value", "MyCustomStoragePropertyValue");

    configuration.put("ia.content.addressed.storage.name", "MyContentAddressedStorage");
    configuration.put("ia.content.addressed.storage.connexion.string", "http://connection");
    configuration.put("ia.content.addressed.storage.pea.name", "MyContentAddressedStoragePaeName");
    configuration.put("ia.content.addressed.storage.pea.value",
        "MyContentAddressedStoragePaeValue");
  }

  private <T> OngoingStubbing<T> mockCollection(Class<T> type, T object) throws IOException {
    when(restClient.followNonPaged(any(LinkContainer.class), anyString(), eq(type)))
        .thenReturn(object);
    return when(restClient.follow(any(LinkContainer.class), anyString(), eq(type)))
        .thenReturn(object);
  }

  protected <T extends NamedLinkContainer> void mockByName(ItemContainer<T> collection, T item)
      throws IOException {
    final AtomicBoolean first = new AtomicBoolean(true);
    when(collection.byName(any())).thenAnswer(invocation -> {
      if (first.get()) {
        first.set(false);
        return null;
      }
      if (!(item instanceof Application)) {
        first.set(true);
      }
      return item;
    });
    when(restClient.refresh(collection)).thenReturn(collection);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeIoException.class)
  public void shouldWrapExceptionDuringConfiguration() throws IOException {
    when(restClient.get(BILLBOARD_URI, Services.class)).thenThrow(IOException.class);
    configureServer();
  }

  private void configureServer() throws IOException {
    configureServer(configuration);
  }

  private ArchiveClient configureServer(Map<String, String> config) throws IOException {
    return archiveClient = ArchiveClients.configuringApplicationUsing(
        new com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedApplicationConfigurer(config),
        connection);
  }

  @Test
  public void shouldIngestSuccessfully() throws IOException {
    configureServer();

    InputStream sip = IOUtils.toInputStream(SOURCE, StandardCharsets.UTF_8);

    ReceptionResponse receptionResponse = new ReceptionResponse();
    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    receptionResponse.setLinks(links);
    when(restClient.post(anyString(), eq(ReceptionResponse.class), any(Part.class), any(Part.class)))
        .thenReturn(receptionResponse);
    when(restClient.post(anyString(), eq(IngestionResponse.class))).thenReturn(ingestionResponse);
    String aipId = "sip001";
    when(ingestionResponse.getAipId()).thenReturn(aipId);

    assertEquals("AIP ID", aipId, archiveClient.ingest(sip));
  }

  @Test
  public void shouldIngestWithIngestDirect() throws IOException {
    Link link = new Link();
    link.setHref(BILLBOARD_URI);
    links.put(InfoArchiveLinkRelations.LINK_INGEST_DIRECT, link);
    configureServer();

    InputStream sip = IOUtils.toInputStream(SOURCE, StandardCharsets.UTF_8);

    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    when(restClient.post(anyString(), eq(IngestionResponse.class), any(Part.class), any(Part.class)))
        .thenReturn(ingestionResponse);
    String aipId = "sip002";
    when(ingestionResponse.getAipId()).thenReturn(aipId);

    assertEquals("AIP ID", aipId, archiveClient.ingestDirect(sip));
  }

  @Test
  public void shouldIngestWithoutIngestDirect() throws IOException {
    configureServer();

    InputStream sip = IOUtils.toInputStream(SOURCE, StandardCharsets.UTF_8);

    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    when(restClient.post(any(), eq(ReceptionResponse.class), any(Part.class), any(Part.class)))
        .thenReturn(new ReceptionResponse());
    when(restClient.post(any(), eq(IngestionResponse.class))).thenReturn(ingestionResponse);
    String aipId = "sip003";
    when(ingestionResponse.getAipId()).thenReturn(aipId);

    assertEquals("AIP ID", aipId, archiveClient.ingestDirect(sip));
  }

  @Test(expected = RuntimeException.class)
  public void ingestShouldThrowRuntimeExceptionWhenConfigureIsNotInvoked() throws IOException {
    InputStream sip = IOUtils.toInputStream(SOURCE, StandardCharsets.UTF_8);
    archiveClient.ingest(sip);
  }

  @Test(expected = RuntimeException.class)
  public void ingestShouldThrowRuntimeExceptionWhenSipIsNull() throws IOException {
    archiveClient.ingest(null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionWhenConfigurationParametersAreNull() throws IOException {
    configureServer(new HashMap<>());
  }

  @Test
  public void shouldCreateApplicationWhenNotFound() throws IOException {
    final AtomicReference<Application> app = new AtomicReference<>(null);
    when(applications.byName(APPLICATION_NAME)).thenAnswer(invocation -> {
      return app.get();
    });
    final AtomicBoolean created = new AtomicBoolean(false);
    when(restClient.createCollectionItem(eq(applications), any(Application.class),
        ArgumentMatchers.<String>any())).thenAnswer(invocation -> {
          created.set(true);
          return null;
        });
    when(restClient.refresh(applications)).thenAnswer(invocation -> {
      if (created.get()) {
        app.set(application);
      }
      return applications;
    });

    configureServer();

    // No error about being unable to create application
  }

  @Test
  public void configure() throws IOException {
    configureServer();

    // Verify no exceptions are thrown
  }

  @Test
  public void shouldRetryWhenTemporarilyUnavailable() throws IOException {
    configuration.put(
        com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.FEDERATION_NAME,
        randomString());
    when(restClient.createCollectionItem(eq(federations), any(XdbFederation.class), eq(LINK_ADD),
        eq(LINK_SELF))).then(invocation -> {
          throw new HttpException(503, "");
        });

    ArchiveClients.configuringApplicationUsing(
        new com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedApplicationConfigurer(configuration),
        connection);

    verify(restClient, times(5)).createCollectionItem(eq(federations), any(XdbFederation.class),
        eq(LINK_ADD), eq(LINK_SELF));
  }

  @Test
  public void shouldSearchSuccessfully() throws IOException {
    SearchResults searchResults = mock(SearchResults.class);
    when(searchResults.getUri("next")).thenReturn(null);
    when(restClient.post(anyString(), eq(SearchResults.class), anyString(), eq(MediaTypes.XML)))
        .thenReturn(searchResults);

    SearchQuery searchQuery = new SearchQuery();
    List<Item> items = new ArrayList<>();
    items.add(new Comparison("from", Operator.EQUAL, ""));
    items.add(new Comparison("recipient", Operator.EQUAL, ""));
    List<String> dates = new ArrayList<>();
    dates.add("2014-04-27");
    dates.add("2016-10-11");
    items.add(new Comparison("sentDate", Operator.BETWEEN, dates));
    items.add(new Comparison("fromCountry", Operator.STARTS_WITH, ""));
    items.add(new Comparison("toCountry", Operator.NOT_EQUAL, ""));
    searchQuery.setItems(items);

    SearchComposition searchComparison = new SearchComposition();
    Map<String, Link> searchComparisonLinks = new HashMap<>();
    Link compositionLink = new Link();
    compositionLink.setHref(randomString());
    searchComparisonLinks.put("self", compositionLink);
    searchComparison.setLinks(searchComparisonLinks);

    configureServer();
    SearchResults result = archiveClient.search(searchQuery, searchComparison);

    assertEquals(searchResults, result);
  }

  @Test
  public void shouldExportSuccessfully() throws IOException {
    UriBuilder uriBuilder = mock(UriBuilder.class);
    String uri = randomString();
    when(uriBuilder.build()).thenReturn(uri);
    when(uriBuilder.addParameter(anyString(), anyString())).thenReturn(uriBuilder);
    when(restClient.uri(any())).thenReturn(uriBuilder);
    OrderItem orderItem = mock(OrderItem.class);
    when(restClient.post(eq(uri), eq(OrderItem.class), anyString())).thenReturn(orderItem);

    SearchResults searchResults = new SearchResults();
    SearchResult searchResult = new SearchResult();
    List<Row> rows = new ArrayList<>();
    Row row = new Row();
    row.setId(randomString());
    rows.add(row);
    searchResult.setRows(rows);
    searchResults.addResult(searchResult);
    ExportConfiguration exportConfiguration = mock(ExportConfiguration.class);
    when(exportConfiguration.getSelfUri()).thenReturn(uri);

    configureServer();
    OrderItem result = archiveClient.export(searchResults, exportConfiguration, randomString());

    assertEquals(orderItem, result);
  }

  @Test(expected = NullPointerException.class)
  public void shouldFetchContentUnsuccessfully() throws IOException {
    configureServer();
    archiveClient.fetchContent(randomString());
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void shouldFetchOrderContentSuccessfully() throws IOException {
    UriBuilder uriBuilder = mock(UriBuilder.class);
    String uri = randomString();
    when(uriBuilder.build()).thenReturn(uri);
    when(uriBuilder.addParameter(anyString(), anyString())).thenReturn(uriBuilder);
    when(restClient.uri(anyString())).thenReturn(uriBuilder);
    ResponseFactory contentResultFactory = mock(ContentResultFactory.class);
    DefaultContentResult contentResult = mock(DefaultContentResult.class);
    when(contentResultFactory.create(any(Response.class), any(Runnable.class)))
        .thenReturn(contentResult);
    when(restClient.get(eq(uri), any(ContentResultFactory.class))).thenReturn(contentResult);
    OrderItem orderItem = new OrderItem();
    Link downloadLink = new Link();
    downloadLink.setHref(randomString());
    orderItem.getLinks().put(LINK_DOWNLOAD, downloadLink);

    configureServer();
    ContentResult result = archiveClient.fetchOrderContent(orderItem);

    assertEquals(contentResult, result);
  }

  @Test
  public void shouldUploadTransformationSuccessfully() throws IOException {
    ExportTransformation exportTransformation = mock(ExportTransformation.class);
    when(exportTransformation.getUri(anyString())).thenReturn(randomString());
    InputStream zip = new ByteArrayInputStream(SOURCE.getBytes(StandardCharsets.UTF_8));

    LinkContainer linkContainer = mock(LinkContainer.class);
    when(linkContainer.getUri(anyString())).thenReturn(randomString());
    when(restClient.post(anyString(), eq(LinkContainer.class), any(Part.class)))
        .thenReturn(linkContainer);

    configureServer();
    LinkContainer result = archiveClient.uploadTransformation(exportTransformation, zip);

    assertEquals(linkContainer, result);
  }

}
