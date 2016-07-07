/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

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
import com.emc.ia.sdk.sip.ingestion.dto.FileSystemRoot;
import com.emc.ia.sdk.sip.ingestion.dto.FileSystemRoots;
import com.emc.ia.sdk.sip.ingestion.dto.Holding;
import com.emc.ia.sdk.sip.ingestion.dto.Holdings;
import com.emc.ia.sdk.sip.ingestion.dto.Ingest;
import com.emc.ia.sdk.sip.ingestion.dto.IngestNode;
import com.emc.ia.sdk.sip.ingestion.dto.IngestNodes;
import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Ingests;
import com.emc.ia.sdk.sip.ingestion.dto.ItemContainer;
import com.emc.ia.sdk.sip.ingestion.dto.Libraries;
import com.emc.ia.sdk.sip.ingestion.dto.Library;
import com.emc.ia.sdk.sip.ingestion.dto.NamedLinkContainer;
import com.emc.ia.sdk.sip.ingestion.dto.Pdi;
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
import com.emc.ia.sdk.sip.ingestion.dto.RetentionPolicies;
import com.emc.ia.sdk.sip.ingestion.dto.RetentionPolicy;
import com.emc.ia.sdk.sip.ingestion.dto.Services;
import com.emc.ia.sdk.sip.ingestion.dto.Space;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootFolder;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootFolders;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootLibraries;
import com.emc.ia.sdk.sip.ingestion.dto.SpaceRootLibrary;
import com.emc.ia.sdk.sip.ingestion.dto.Spaces;
import com.emc.ia.sdk.sip.ingestion.dto.Store;
import com.emc.ia.sdk.sip.ingestion.dto.Stores;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;
import com.emc.ia.sdk.sip.ingestion.dto.query.Comparision;
import com.emc.ia.sdk.sip.ingestion.dto.query.Operator;
import com.emc.ia.sdk.sip.ingestion.dto.query.SearchQuery;
import com.emc.ia.sdk.support.http.Part;
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

  private final Map<String, Link> links = new HashMap<String, Link>();
  private final Map<String, String> configuration = new HashMap<String, String>();
  private final RestClient restClient = mock(RestClient.class);
  private final InfoArchiveRestClient archiveClient = new InfoArchiveRestClient(restClient);
  private Applications applications;
  private Application application;
  private Aics aics;
  private Aic aic;

  @Before
  public void init() throws IOException {
    prepareConfiguration();

    archiveClient.setConfiguration(configuration);

    Services resource = new Services();
    Link link = mock(Link.class);
    Tenant tenant = new Tenant();
    application = new Application();
    applications = mock(Applications.class);
    Federations federations = mock(Federations.class);
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
    Queries queries = mock(Queries.class);
    Quotas quotas = mock(Quotas.class);
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
    when(link.getHref()).thenReturn(BILLBOARD_URI);
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

    mockByName(federations, new Federation());
    mockByName(databases, new Database());
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

    configuration.put("ia.aic.name", "MyAic");
    configuration.put("ia.aic.criteria.name", "name");
    configuration.put("ia.aic.criteria.label", "name");
    configuration.put("ia.aic.criteria.type", "STRING");
    configuration.put("ia.aic.criteria.pkeyminattr", "");
    configuration.put("ia.aic.criteria.pkeymaxattr", "");
    configuration.put("ia.aic.criteria.pkeyvaluesattr", "");
    configuration.put("ia.aic.criteria.indexed", "true");

    configuration.put("ia.query.name", "Query");
    configuration.put("ia.query.Query.namespace.prefix", "n");
    configuration.put("ia.query.Query.namespace.uri", NAMESPACE);
    configuration.put("ia.query.Query.result.root.element", "messages");
    configuration.put("ia.query.Query.result.root.ns.enabled", "true");
    configuration.put("ia.query.Query.result.schema", NAMESPACE);

    configuration.put("ia.query.Query.xdbpdi.entity.path", "/n:object/n:objects");
    configuration.put("ia.query.Query.xdbpdi.schema", NAMESPACE);
    configuration.put("ia.query.Query.xdbpdi.template", "return $aiu");

    String queryPrefix = "ia.query.Query.xdbpdi[";
    configuration.put(queryPrefix + NAMESPACE + "].operand.name", "name");
    configuration.put(queryPrefix + NAMESPACE + "].operand.path", "n:name");
    configuration.put(queryPrefix + NAMESPACE + "].operand.type", "STRING");
    configuration.put(queryPrefix + NAMESPACE + "].operand.index", "true");

    configuration.put("ia.quota.name", "Quota");
    configuration.put("ia.quota.aiu", "0");
    configuration.put("ia.quota.aip", "0");
    configuration.put("ia.quota.dip", "0");
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

  @Test(expected = RuntimeException.class)
  public void shouldThrowExceptionWileConfiguring() {
    archiveClient.configure(null);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = RuntimeIoException.class)
  public void shouldWrapExceptionDuringConfiguration() throws IOException {
    when(restClient.get(BILLBOARD_URI, Services.class)).thenThrow(IOException.class);
    archiveClient.configure(configuration);
  }

  @Test
  public void shouldIngestSuccessfully() throws IOException {
    archiveClient.setConfiguration(configuration);
    String aipsUri = randomString();
    when(application.getUri(LINK_AIPS)).thenReturn(aipsUri);
    archiveClient.getConfigurationState()
      .setApplication(application);
    archiveClient.cacheAipsUri();

    String source = "This is the source of my input stream";
    InputStream sip = IOUtils.toInputStream(source, "UTF-8");

    ReceptionResponse receptionResponse = new ReceptionResponse();
    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    receptionResponse.setLinks(links);
    when(restClient.post(anyString(), eq(ReceptionResponse.class), any(Part.class), any(Part.class)))
      .thenReturn(receptionResponse);
    when(restClient.put(anyString(), eq(IngestionResponse.class))).thenReturn(ingestionResponse);
    when(ingestionResponse.getAipId()).thenReturn("sip001");

    assertEquals(archiveClient.ingest(sip), "sip001");
  }

  @Test(expected = RuntimeException.class)
  public void ingestShouldThrowRuntimeExceptionWhenConfigureIsNotInvoked() throws IOException {
    String source = "This is the source of my input stream";
    InputStream sip = IOUtils.toInputStream(source, "UTF-8");
    archiveClient.ingest(sip);
  }

  @Test(expected = RuntimeException.class)
  public void ingestShouldThrowRuntimeExceptionWhenSipIsNull() throws IOException {
    archiveClient.ingest(null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionWhenConfigurationParametersAreNull() throws IOException {
    Map<String, String> config = new HashMap<String, String>();
    archiveClient.configure(config);
  }

  @Test
  public void shouldCreateApplicationWhenNotFound() throws IOException {
    final AtomicReference<Application> app = new AtomicReference<Application>(null);
    when(applications.byName(APPLICATION_NAME)).thenAnswer(invocation -> {
      return app.get();
    });
    final AtomicBoolean created = new AtomicBoolean(false);
    when(restClient.createCollectionItem(eq(applications), eq(LINK_ADD), any(Application.class)))
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

    archiveClient.ensureApplication();
  }

  @Test
  public void shouldQuerySuccessfully() throws IOException, URISyntaxException {
    when(aics.getItems()).thenReturn(Arrays.asList(aic)
      .stream());
    Link dipLink = new Link();
    dipLink.setHref(randomString());
    aic.getLinks()
      .put(LINK_DIP, dipLink);
    aic.setName("MyAic");
    archiveClient.cacheDipUris();

    SearchQuery query = new SearchQuery();
    query.getItems()
      .add(new Comparision("variable", Operator.EQUAL, "value"));
    String aicName = "MyAic";
    String schema = NAMESPACE;
    archiveClient.query(query, aicName, schema, 10);
  }

  @Test
  public void configure() {
    archiveClient.configure(configuration);
  }

}
