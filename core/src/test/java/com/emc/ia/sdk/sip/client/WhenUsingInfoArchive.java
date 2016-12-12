/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.stubbing.OngoingStubbing;

import com.emc.ia.sdk.configurer.ArchiveClients;
import com.emc.ia.sdk.configurer.InfoArchiveConfiguration;
import com.emc.ia.sdk.sip.client.dto.Aic;
import com.emc.ia.sdk.sip.client.dto.Aics;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Contents;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Databases;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolder;
import com.emc.ia.sdk.sip.client.dto.FileSystemFolders;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.client.dto.FileSystemRoots;
import com.emc.ia.sdk.sip.client.dto.Holding;
import com.emc.ia.sdk.sip.client.dto.Holdings;
import com.emc.ia.sdk.sip.client.dto.Ingest;
import com.emc.ia.sdk.sip.client.dto.IngestNode;
import com.emc.ia.sdk.sip.client.dto.IngestNodes;
import com.emc.ia.sdk.sip.client.dto.IngestionResponse;
import com.emc.ia.sdk.sip.client.dto.Ingests;
import com.emc.ia.sdk.sip.client.dto.ItemContainer;
import com.emc.ia.sdk.sip.client.dto.Libraries;
import com.emc.ia.sdk.sip.client.dto.Library;
import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;
import com.emc.ia.sdk.sip.client.dto.OrderItem;
import com.emc.ia.sdk.sip.client.dto.Pdi;
import com.emc.ia.sdk.sip.client.dto.PdiSchema;
import com.emc.ia.sdk.sip.client.dto.PdiSchemas;
import com.emc.ia.sdk.sip.client.dto.Pdis;
import com.emc.ia.sdk.sip.client.dto.Queries;
import com.emc.ia.sdk.sip.client.dto.Query;
import com.emc.ia.sdk.sip.client.dto.Quota;
import com.emc.ia.sdk.sip.client.dto.Quotas;
import com.emc.ia.sdk.sip.client.dto.ReceiverNode;
import com.emc.ia.sdk.sip.client.dto.ReceiverNodes;
import com.emc.ia.sdk.sip.client.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.client.dto.ResultConfigurationHelper;
import com.emc.ia.sdk.sip.client.dto.ResultConfigurationHelpers;
import com.emc.ia.sdk.sip.client.dto.RetentionPolicies;
import com.emc.ia.sdk.sip.client.dto.RetentionPolicy;
import com.emc.ia.sdk.sip.client.dto.Row;
import com.emc.ia.sdk.sip.client.dto.Search;
import com.emc.ia.sdk.sip.client.dto.SearchComposition;
import com.emc.ia.sdk.sip.client.dto.SearchCompositions;
import com.emc.ia.sdk.sip.client.dto.SearchResult;
import com.emc.ia.sdk.sip.client.dto.SearchResults;
import com.emc.ia.sdk.sip.client.dto.Searches;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolder;
import com.emc.ia.sdk.sip.client.dto.SpaceRootFolders;
import com.emc.ia.sdk.sip.client.dto.SpaceRootLibraries;
import com.emc.ia.sdk.sip.client.dto.SpaceRootLibrary;
import com.emc.ia.sdk.sip.client.dto.Spaces;
import com.emc.ia.sdk.sip.client.dto.Store;
import com.emc.ia.sdk.sip.client.dto.Stores;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.sip.client.dto.XForm;
import com.emc.ia.sdk.sip.client.dto.XForms;
import com.emc.ia.sdk.sip.client.dto.export.ExportConfiguration;
import com.emc.ia.sdk.sip.client.dto.export.ExportConfigurations;
import com.emc.ia.sdk.sip.client.dto.export.ExportPipeline;
import com.emc.ia.sdk.sip.client.dto.export.ExportPipelines;
import com.emc.ia.sdk.sip.client.dto.export.ExportTransformation;
import com.emc.ia.sdk.sip.client.dto.export.ExportTransformations;
import com.emc.ia.sdk.sip.client.dto.query.Comparison;
import com.emc.ia.sdk.sip.client.dto.query.Item;
import com.emc.ia.sdk.sip.client.dto.query.Operator;
import com.emc.ia.sdk.sip.client.dto.query.SearchQuery;
import com.emc.ia.sdk.sip.client.dto.result.searchconfig.AllSearchComponents;
import com.emc.ia.sdk.sip.client.rest.ContentResultFactory;
import com.emc.ia.sdk.sip.client.rest.DefaultContentResult;
import com.emc.ia.sdk.sip.client.rest.DefaultQueryResult;
import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.sip.client.rest.QueryResultFactory;
import com.emc.ia.sdk.support.datetime.Clock;
import com.emc.ia.sdk.support.http.HttpException;
import com.emc.ia.sdk.support.http.MediaTypes;
import com.emc.ia.sdk.support.http.Part;
import com.emc.ia.sdk.support.http.Response;
import com.emc.ia.sdk.support.http.UriBuilder;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.Link;
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.RestClient;
import com.emc.ia.sdk.support.test.TestCase;


public class WhenUsingInfoArchive extends TestCase implements InfoArchiveLinkRelations {

  private static final String BILLBOARD_URI = "http://foo.com/bar";
  private static final String AUTH_TOKEN = "XYZ123ABC";
  private static final String APPLICATION_NAME = "ApPlIcAtIoN";
  private static final String TENANT_NAME = "TeNaNt";
  private static final String NAMESPACE = "urn:SoMeNaMeSpAcE";

  private static final String SOURCE = "This is the source of my input stream";

  private final Map<String, Link> links = new HashMap<>();
  private final Map<String, String> configuration = new HashMap<>();
  private final RestClient restClient = mock(RestClient.class);
  private ArchiveClient archiveClient;
  private Applications applications;
  private Application application;
  private Aics aics;
  private Aic aic;
  private Federations federations;

  @Before
  public void init() throws IOException { // NOPMD NcssMethodCount
    prepareConfiguration();

    Services resource = new Services();
    Link link = mock(Link.class);
    Tenant tenant = new Tenant();
    application = new Application();
    applications = mock(Applications.class);
    ExportConfigurations exportConfigurations = mock(ExportConfigurations.class);
    ExportPipelines exportPipelines = mock(ExportPipelines.class);
    ExportTransformations exportTransformations = mock(ExportTransformations.class);
    federations = mock(Federations.class);
    Spaces spaces = mock(Spaces.class);
    Databases databases = mock(Databases.class);
    FileSystemRoots fileSystemRoots = mock(FileSystemRoots.class);
    FileSystemRoot fileSystemRoot = new FileSystemRoot();
    when(fileSystemRoots.first()).thenReturn(fileSystemRoot);
    Holdings holdings = mock(Holdings.class);
    ReceiverNodes receiverNodes = mock(ReceiverNodes.class);
    SpaceRootLibraries spaceRootLibraries = mock(SpaceRootLibraries.class);
    SpaceRootFolders rootFolders = mock(SpaceRootFolders.class);
    FileSystemFolders systemFolders = mock(FileSystemFolders.class);
    Stores stores = mock(Stores.class);
    IngestNodes ingestionNodes = mock(IngestNodes.class);
    RetentionPolicies retentionPolicies = mock(RetentionPolicies.class);
    Pdis pdis = mock(Pdis.class);
    PdiSchemas pdiSchemas = mock(PdiSchemas.class);
    Ingests ingests = mock(Ingests.class);
    Libraries libraries = mock(Libraries.class);
    Contents contents = new Contents();
    aics = mock(Aics.class);
    LinkContainer aips = new LinkContainer();
    Queries queries = mock(Queries.class);
    Quotas quotas = mock(Quotas.class);
    ResultConfigurationHelpers helpers = mock(ResultConfigurationHelpers.class);
    Searches searches = mock(Searches.class);
    SearchCompositions compositions = mock(SearchCompositions.class);
    XForms xForms = mock(XForms.class);
    XForm xForm = mock(XForm.class);

    aic = new Aic();

    links.put(InfoArchiveLinkRelations.LINK_TENANT, link);
    links.put(InfoArchiveLinkRelations.LINK_APPLICATIONS, link);
    links.put(InfoArchiveLinkRelations.LINK_AIPS, link);
    links.put(InfoArchiveLinkRelations.LINK_INGEST, link);
    resource.setLinks(links);
    tenant.setLinks(links);
    application.setLinks(links);

    contents.getLinks()
      .put(LINK_DOWNLOAD, new Link());
    when(restClient.follow(any(LinkContainer.class), eq(LINK_CONTENTS), eq(Contents.class))).thenReturn(contents);

    when(restClient.get(BILLBOARD_URI, Services.class)).thenReturn(resource);
    when(restClient.follow(resource, LINK_TENANT, Tenant.class)).thenReturn(tenant);
    when(link.getHref()).thenReturn(BILLBOARD_URI);
    mockCollection(ExportConfigurations.class, exportConfigurations);
    mockCollection(ExportPipelines.class, exportPipelines);
    mockCollection(ExportTransformations.class, exportTransformations);
    when(restClient.follow(application, InfoArchiveLinkRelations.LINK_AIPS, LinkContainer.class)).thenReturn(aips);
    aips.setLinks(links);

    mockCollection(Applications.class, applications);
    mockCollection(Federations.class, federations);
    mockCollection(Spaces.class, spaces);
    mockCollection(Databases.class, databases);
    mockCollection(FileSystemRoots.class, fileSystemRoots);
    mockCollection(Holdings.class, holdings);
    mockCollection(ReceiverNodes.class, receiverNodes);
    mockCollection(SpaceRootLibraries.class, spaceRootLibraries);
    mockCollection(SpaceRootFolders.class, rootFolders);
    mockCollection(FileSystemFolders.class, systemFolders);
    mockCollection(Stores.class, stores);
    mockCollection(IngestNodes.class, ingestionNodes);
    mockCollection(RetentionPolicies.class, retentionPolicies);
    mockCollection(Pdis.class, pdis);
    mockCollection(PdiSchemas.class, pdiSchemas);
    mockCollection(Ingests.class, ingests);
    mockCollection(Libraries.class, libraries);
    mockCollection(Aics.class, aics);
    mockCollection(Quotas.class, quotas);
    mockCollection(Queries.class, queries);
    mockCollection(ResultConfigurationHelpers.class, helpers);
    mockCollection(Searches.class, searches);
    mockCollection(SearchCompositions.class, compositions);
    mockCollection(XForms.class, xForms);
    when(restClient.createCollectionItem(any(LinkContainer.class), any(XForm.class), eq(LINK_SELF))).thenReturn(xForm);

    mockByName(federations, new Federation());
    mockByName(databases, new Database());
    mockByName(exportConfigurations, new ExportConfiguration());
    mockByName(exportPipelines, new ExportPipeline());
    mockByName(exportTransformations, new ExportTransformation());
    mockByName(applications, application);
    mockByName(spaces, new Space());
    mockByName(spaceRootLibraries, new SpaceRootLibrary());
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
    mockByName(libraries, new Library());
    mockByName(holdings, new Holding());
    mockByName(aics, aic);
    mockByName(quotas, new Quota());
    mockByName(queries, new Query());
    mockByName(helpers, new ResultConfigurationHelper());
    mockByName(searches, new Search());
    mockByName(compositions, new SearchComposition());
    mockByName(xForms, new XForm());

    when(restClient.put(anyString(), eq(SearchComposition.class), any(AllSearchComponents.class)))
        .thenReturn(new SearchComposition());

    when(aics.getItems()).thenReturn(Stream.of(aic));

  }

  private void prepareConfiguration() {
    configuration.put(InfoArchiveConfiguration.SERVER_URI, BILLBOARD_URI);
    configuration.put(InfoArchiveConfiguration.SERVER_AUTENTICATON_TOKEN, AUTH_TOKEN);
    configuration.put(InfoArchiveConfiguration.TENANT_NAME, TENANT_NAME);
    configuration.put(InfoArchiveConfiguration.DATABASE_NAME, APPLICATION_NAME);
    configuration.put(InfoArchiveConfiguration.DATABASE_ADMIN_PASSWORD, APPLICATION_NAME);
    configuration.put(InfoArchiveConfiguration.APPLICATION_NAME, APPLICATION_NAME);
    configuration.put(InfoArchiveConfiguration.HOLDING_NAME, APPLICATION_NAME);
    configuration.put(InfoArchiveConfiguration.RETENTION_POLICY_NAME, APPLICATION_NAME);
    configuration.put(InfoArchiveConfiguration.PDI_XML, "");
    configuration.put(InfoArchiveConfiguration.PDI_SCHEMA_NAME, APPLICATION_NAME);
    configuration.put(InfoArchiveConfiguration.PDI_SCHEMA, "");
    configuration.put(InfoArchiveConfiguration.INGEST_XML, "");
    configuration.put(InfoArchiveConfiguration.SEARCH_DESCRIPTION, "Default emails search");
    configuration.put(InfoArchiveConfiguration.SEARCH_NESTED, Boolean.FALSE.toString());
    configuration.put(InfoArchiveConfiguration.SEARCH_NAME, "emailsSearch");
    configuration.put(InfoArchiveConfiguration.SEARCH_STATE, "DRAFT");
    configuration.put(InfoArchiveConfiguration.SEARCH_INUSE, Boolean.FALSE.toString());
    configuration.put(InfoArchiveConfiguration.SEARCH_COMPOSITION_NAME, "DefaultSearchComposition");
    configuration.put(InfoArchiveConfiguration.SEARCH_COMPOSITION_XFORM_NAME, "Test Search Form");
    configuration.put(InfoArchiveConfiguration.SEARCH_DEFAULT_RESULT_MASTER, "");
    configuration.put(InfoArchiveConfiguration.SEARCH_DEFAULT_SEARCH, "");

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
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.path", "n:sender/n:email");
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.type", "STRING");
    configuration.put("ia.search.emailsSearch.composition.Set 1.result.main.sort", "NONE");

    configuration.put("ia.exportpipeline.names", "ExportPipeline");
    configuration.put("ia.exportpipeline.ExportPipeline.composite", "NONE");
    configuration.put("ia.exportpipeline.ExportPipeline.content", "<pipeline></pipeline>");
    configuration.put("ia.exportpipeline.ExportPipeline.description", "gzip envelope for xsl csv export");
    configuration.put("ia.exportpipeline.ExportPipeline.envelopeformat", "gzip");
    configuration.put("ia.exportpipeline.ExportPipeline.includescontent", Boolean.TRUE.toString());
    configuration.put("ia.exportpipeline.ExportPipeline.outputformat", "csv");
    configuration.put("ia.exportpipeline.ExportPipeline.inputformat", "ROW_COLUMN");
    configuration.put("ia.exportpipeline.ExportPipeline.type", "NONE");
    configuration.put("ia.exportpipeline.ExportPipeline.collectionbased", Boolean.FALSE.toString());

    configuration.put("ia.exportconfig.names", "ExportConfiguration");
    configuration.put("ia.exportconfig.ExportConfiguration.type", "ASYNCHRONOUS");
    configuration.put("ia.exportconfig.ExportConfiguration.pipeline", "ExportPipeline");

    configuration.put("ia.exporttransformation.names", "ExportTransformation");
    configuration.put("ia.exporttransformation.ExportTransformation.description", "csv xsl transformation");
    configuration.put("ia.exporttransformation.ExportTransformation.type", "XSLT");
    configuration.put("ia.exporttransformation.ExportTransformation.mainpath", "search-results-csv.xsl");
  }

  private <T> OngoingStubbing<T> mockCollection(Class<T> type, T object) throws IOException {
    return when(restClient.follow(any(LinkContainer.class), anyString(), eq(type))).thenReturn(object);
  }

  protected <T extends NamedLinkContainer> void mockByName(ItemContainer<T> collection, T item) throws IOException {
    final AtomicBoolean first = new AtomicBoolean(true);
    when(collection.byName(anyString())).thenAnswer(invocation -> {
      if (first.get()) {
        first.set(false);
        return null;
      }
      return item;
    });
    when(restClient.refresh(collection)).thenReturn(collection);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeIoException.class)
  public void shouldWrapExceptionDuringConfiguration() throws IOException {
    when(restClient.get(BILLBOARD_URI, Services.class)).thenThrow(IOException.class);
    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
  }

  @Test
  public void shouldIngestSuccessfully() throws IOException {
    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);

    InputStream sip = IOUtils.toInputStream(SOURCE, StandardCharsets.UTF_8);

    ReceptionResponse receptionResponse = new ReceptionResponse();
    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    receptionResponse.setLinks(links);
    when(restClient.post(anyString(), eq(ReceptionResponse.class), any(Part.class), any(Part.class)))
      .thenReturn(receptionResponse);
    when(restClient.put(anyString(), eq(IngestionResponse.class))).thenReturn(ingestionResponse);
    when(ingestionResponse.getAipId()).thenReturn("sip001");

    assertEquals(archiveClient.ingest(sip), "sip001");
  }

  @Test
  public void shouldIngestWithIngestDirect() throws IOException {
    Link link = new Link();
    link.setHref(BILLBOARD_URI);
    links.put(InfoArchiveLinkRelations.LINK_INGEST_DIRECT, link);
    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);

    InputStream sip = IOUtils.toInputStream(SOURCE, StandardCharsets.UTF_8);

    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    when(restClient.post(anyString(), eq(IngestionResponse.class), any(Part.class), any(Part.class)))
        .thenReturn(ingestionResponse);
    when(ingestionResponse.getAipId()).thenReturn("sip002");

    assertEquals(archiveClient.ingestDirect(sip), "sip002");
  }

  @Test
  public void shouldIngestWithoutIngestDirect() throws IOException {
    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);

    InputStream sip = IOUtils.toInputStream(SOURCE, StandardCharsets.UTF_8);

    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    when(restClient.post(anyString(), eq(ReceptionResponse.class), any(Part.class), any(Part.class)))
      .thenReturn(new ReceptionResponse());
    when(restClient.put(anyString(), eq(IngestionResponse.class))).thenReturn(ingestionResponse);
    when(ingestionResponse.getAipId()).thenReturn("sip003");

    assertEquals(archiveClient.ingestDirect(sip), "sip003");
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
    Map<String, String> config = new HashMap<>();
    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(config, restClient);
  }

  @Test
  public void shouldCreateApplicationWhenNotFound() throws IOException {
    final AtomicReference<Application> app = new AtomicReference<>(null);
    when(applications.byName(APPLICATION_NAME)).thenAnswer(invocation -> {
      return app.get();
    });
    final AtomicBoolean created = new AtomicBoolean(false);
    when(restClient.createCollectionItem(eq(applications), any(Application.class), Matchers.<String>anyVararg()))
        .thenAnswer(invocation -> {
      created.set(true);
      return null;
    });
    when(restClient.refresh(applications)).thenAnswer(invocation -> {
      if (created.get()) {
        app.set(application);
      }
      return applications;
    });

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);

    // No error about being unable to create application
  }

  @Test
  public void shouldQuerySuccessfully() throws IOException {
    when(aics.getItems()).thenReturn(Stream.of(aic));
    Link dipLink = new Link();
    dipLink.setHref(randomString());
    aic.getLinks().put(LINK_DIP, dipLink);
    aic.setName("MyAic");
    UriBuilder uriBuilder = mock(UriBuilder.class);
    String uri = randomString();
    when(uriBuilder.build()).thenReturn(uri);
    when(uriBuilder.addParameter(anyString(), anyString())).thenReturn(uriBuilder);
    when(restClient.uri(anyString())).thenReturn(uriBuilder);
    QueryResultFactory queryResultFactory = mock(QueryResultFactory.class);
    DefaultQueryResult queryResult = mock(DefaultQueryResult.class);
    when(queryResultFactory.create(any(Response.class))).thenReturn(queryResult);
    when(restClient.get(eq(uri), any(QueryResultFactory.class))).thenReturn(queryResult);

    SearchQuery query = new SearchQuery();
    query.getItems().add(new Comparison("variable", Operator.EQUAL, "value"));
    String aicName = "MyAic";
    String schema = NAMESPACE;

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    QueryResult result = archiveClient.query(query, aicName, schema, 10);

    assertEquals(queryResult, result);
  }

  @Test
  public void configure() {
    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);

    // Verify no exceptions are thrown
  }

  @Test
  public void shouldRetryWhenTemporarilyUnavailable() throws IOException {
    configuration.put(InfoArchiveConfiguration.FEDERATION_NAME, randomString());
    when(restClient.createCollectionItem(eq(federations), any(Federation.class), eq(LINK_ADD), eq(LINK_SELF)))
        .then(invocation -> {
      throw new HttpException(503, "");
    });

    ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient, mock(Clock.class));

    verify(restClient, times(5)).createCollectionItem(eq(federations), any(Federation.class), eq(LINK_ADD),
        eq(LINK_SELF));
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

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    SearchResults result = archiveClient.search(searchQuery, searchComparison);

    assertEquals(searchResults, result);
  }

  @Test
  public void shouldExportSuccessfully() throws IOException {
    UriBuilder uriBuilder = mock(UriBuilder.class);
    String uri = randomString();
    when(uriBuilder.build()).thenReturn(uri);
    when(uriBuilder.addParameter(anyString(), anyString())).thenReturn(uriBuilder);
    when(restClient.uri(anyString())).thenReturn(uriBuilder);
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

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    OrderItem result = archiveClient.export(searchResults, exportConfiguration, randomString());

    assertEquals(orderItem, result);
  }

  @Test
  public void shouldExportAndWaitSuccessfully() throws IOException {
    UriBuilder uriBuilder = mock(UriBuilder.class);
    String uri = randomString();
    when(uriBuilder.build()).thenReturn(uri);
    when(uriBuilder.addParameter(anyString(), anyString())).thenReturn(uriBuilder);
    when(restClient.uri(anyString())).thenReturn(uriBuilder);
    OrderItem orderItem = mock(OrderItem.class);
    when(orderItem.getUri(anyString())).thenReturn(randomString());
    when(restClient.post(eq(uri), eq(OrderItem.class), anyString())).thenReturn(orderItem);
    when(restClient.get(anyString(), eq(OrderItem.class))).thenReturn(orderItem);

    SearchResults searchResults = new SearchResults();
    ExportConfiguration exportConfiguration = mock(ExportConfiguration.class);
    when(exportConfiguration.getSelfUri()).thenReturn(uri);

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    OrderItem result = archiveClient.exportAndWait(searchResults, exportConfiguration, randomString(), 6000);

    assertEquals(orderItem, result);
  }

  @Test
  public void shouldExportAndWaitUnsuccessfully() throws IOException {
    UriBuilder uriBuilder = mock(UriBuilder.class);
    String uri = randomString();
    when(uriBuilder.build()).thenReturn(uri);
    when(uriBuilder.addParameter(anyString(), anyString())).thenReturn(uriBuilder);
    when(restClient.uri(anyString())).thenReturn(uriBuilder);
    OrderItem orderItem = mock(OrderItem.class);
    when(orderItem.getUri(anyString())).thenReturn(null);
    when(restClient.post(eq(uri), eq(OrderItem.class), anyString())).thenReturn(orderItem);
    when(restClient.get(anyString(), eq(OrderItem.class))).thenReturn(orderItem);

    SearchResults searchResults = new SearchResults();
    ExportConfiguration exportConfiguration = mock(ExportConfiguration.class);
    when(exportConfiguration.getSelfUri()).thenReturn(uri);

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    OrderItem result = archiveClient.exportAndWait(searchResults, exportConfiguration, randomString(), randomInt(3000));

    assertEquals(orderItem, result);
  }

  @Test(expected = NullPointerException.class)
  public void shouldFetchContentUnsuccessfully() throws IOException {
    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    archiveClient.fetchContent(randomString());
  }

  @Test
  public void shouldFetchOrderContentSuccessfully() throws IOException {
    UriBuilder uriBuilder = mock(UriBuilder.class);
    String uri = randomString();
    when(uriBuilder.build()).thenReturn(uri);
    when(uriBuilder.addParameter(anyString(), anyString())).thenReturn(uriBuilder);
    when(restClient.uri(anyString())).thenReturn(uriBuilder);
    ContentResultFactory contentResultFactory = mock(ContentResultFactory.class);
    DefaultContentResult contentResult = mock(DefaultContentResult.class);
    when(contentResultFactory.create(any(Response.class))).thenReturn(contentResult);
    when(restClient.get(eq(uri), any(ContentResultFactory.class))).thenReturn(contentResult);
    OrderItem orderItem = new OrderItem();

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    ContentResult result = archiveClient.fetchOrderContent(orderItem);

    assertEquals(contentResult, result);
  }

  @Test
  public void shouldUploadTransformationFileSuccessfully() throws IOException {
    ExportTransformation exportTransformation = mock(ExportTransformation.class);
    when(exportTransformation.getUri(anyString())).thenReturn(randomString());
    InputStream zip = new ByteArrayInputStream(SOURCE.getBytes(StandardCharsets.UTF_8));

    LinkContainer linkContainer = mock(LinkContainer.class);
    when(linkContainer.getUri(anyString())).thenReturn(randomString());
    when(restClient.post(anyString(), eq(LinkContainer.class), any(Part.class))).thenReturn(linkContainer);

    archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(configuration, restClient);
    LinkContainer result = archiveClient.uploadTransformationFile(exportTransformation, zip);

    assertEquals(linkContainer, result);
  }

}
