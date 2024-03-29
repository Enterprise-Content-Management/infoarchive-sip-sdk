/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.impl;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.client.api.ContentResult;
import com.opentext.ia.sdk.client.api.InfoArchiveLinkRelations;
import com.opentext.ia.sdk.dto.IngestionResponse;
import com.opentext.ia.sdk.dto.JobDefinition;
import com.opentext.ia.sdk.dto.JobDefinitions;
import com.opentext.ia.sdk.dto.JobInstance;
import com.opentext.ia.sdk.dto.JobInstanceFilter;
import com.opentext.ia.sdk.dto.OrderItem;
import com.opentext.ia.sdk.dto.ReceptionResponse;
import com.opentext.ia.sdk.dto.Row;
import com.opentext.ia.sdk.dto.SearchComposition;
import com.opentext.ia.sdk.dto.SearchDataBuilder;
import com.opentext.ia.sdk.dto.SearchResult;
import com.opentext.ia.sdk.dto.SearchResults;
import com.opentext.ia.sdk.dto.Services;
import com.opentext.ia.sdk.dto.export.ExportConfiguration;
import com.opentext.ia.sdk.dto.export.ExportTransformation;
import com.opentext.ia.sdk.dto.query.Comparison;
import com.opentext.ia.sdk.dto.query.SearchQuery;
import com.opentext.ia.sdk.support.http.BinaryPart;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.ResponseFactory;
import com.opentext.ia.sdk.support.http.TextPart;
import com.opentext.ia.sdk.support.http.rest.LinkContainer;
import com.opentext.ia.sdk.support.http.rest.RestClient;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient, InfoArchiveLinkRelations {

  private final ResponseFactory<ContentResult> contentResultFactory = new ContentResultFactory();

  private final RestClient restClient;
  private final ApplicationIngestionResourcesCache resourceCache;

  public InfoArchiveRestClient(RestClient restClient, ApplicationIngestionResourcesCache resourceCache) {
    this.restClient = restClient;
    this.resourceCache = resourceCache;
  }

  @Override
  public String ingest(InputStream sip) throws IOException {
    ReceptionResponse response = restClient.post(resourceCache.getAipResourceUri(), ReceptionResponse.class,
        new TextPart("format", "sip_zip"), new BinaryPart("sip", sip, "IASIP.zip"));
    String ingestUri = response.getUri(LINK_INGEST);
    // There was a backwards incompatible change between 4.2 and 4.3 from PUT to POST
    if (resourceCache.isVersionOrEarlier("4.2")) {
      return restClient.put(ingestUri, IngestionResponse.class).getAipId();
    }
    return restClient.post(ingestUri, IngestionResponse.class).getAipId();
  }

  @Override
  public String ingestDirect(InputStream sip) throws IOException {
    String ingestDirectUri = resourceCache.getAipIngestDirectResourceUri();
    if (ingestDirectUri == null) {
      // Fall back to previous approach
      return ingest(sip);
    }
    return restClient.post(ingestDirectUri, IngestionResponse.class, new TextPart("format", "sip_zip"),
        new BinaryPart("sip", sip, "IASIP.zip")).getAipId();
  }

  /**
   * Fetch the content for the specified content id.
   * @param contentId The id of the content to fetch.
   * @return A ContentResult
   * @throws IOException When an I/O error occurs
   * @deprecated Will be removed without replacement in a future version
   */
  @Override
  @Deprecated
  public ContentResult fetchContent(String contentId) throws IOException {
    try {
      String contentResource = resourceCache.getCiResourceUri();
      URIBuilder builder = new URIBuilder(contentResource);
      builder.setParameter("cid", contentId);
      URI uri = builder.build();
      return restClient.get(uri.toString(), contentResultFactory);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Failed to create content resource uri.", e);
    }
  }

  /**
   * Fetch the content for the specified order item.
   * @param orderItem The order item.
   * @return A ContentResult
   * @throws IOException When an I/O error occurs
   * @deprecated Will be removed without replacement in a future version
   */
  @Override
  @Deprecated
  public ContentResult fetchOrderContent(OrderItem orderItem) throws IOException {
    String downloadUri = Objects.requireNonNull(
        Objects.requireNonNull(orderItem, "Missing order item").getUri(LINK_DOWNLOAD), "Missing download URI");
    String fetchUri = restClient.uri(downloadUri)
        .addParameter("downloadToken", "")
        .build();
    return restClient.get(fetchUri, contentResultFactory);
  }

  @Override
  public SearchResults search(SearchQuery searchQuery, SearchComposition searchComposition) throws IOException {
    String uri = searchComposition.getSelfUri();
    String searchCriteria = toSearchCriteria(searchQuery);
    SearchResults result = restClient.post(uri, SearchResults.class, searchCriteria, MediaTypes.XML);
    uri = result.getUri("next");
    while (uri != null) {
      SearchResults onePageOfSearchResults = restClient.post(uri, SearchResults.class, searchCriteria, MediaTypes.XML);
      onePageOfSearchResults.getResults().forEach(result::addResult);
      uri = onePageOfSearchResults.getUri("next");
    }
    return result;
  }

  private String toSearchCriteria(SearchQuery searchQuery) {
    SearchDataBuilder searchDataBuilder = SearchDataBuilder.builder();
    searchQuery.getItems().stream()
        .filter(item -> item instanceof Comparison)
        .map(Comparison.class::cast)
        .forEach(comparison -> addComparison(comparison, searchDataBuilder));
    return searchDataBuilder.build()
        .replaceFirst("<\\?.*\\?>", "")
        .replace(" ", "")
        .replace(System.lineSeparator(), "");
  }

  private void addComparison(Comparison comparison, SearchDataBuilder searchDataBuilder) {
    switch (comparison.getOperator()) {
      case EQUAL: searchDataBuilder.isEqual(comparison.getName(), comparison.getValue().get(0));
        break;
      case NOT_EQUAL:
        searchDataBuilder.isNotEqual(comparison.getName(), comparison.getValue().get(0));
        break;
      case STARTS_WITH:
        searchDataBuilder.startsWith(comparison.getName(), comparison.getValue().get(0));
        break;
      case BETWEEN:
        searchDataBuilder.between(comparison.getName(), comparison.getValue().get(0), comparison.getValue().get(1));
        break;
      default:
        break;
    }
  }

  @Override
  public OrderItem export(SearchResults searchResults, ExportConfiguration exportConfiguration, String outputName)
      throws IOException {
    String fullOutputName = outputName + '_' + Long.toString(new Date().getTime());
    String exportUri = restClient.uri(searchResults.getUri(LINK_EXPORT))
        .addParameter("name", fullOutputName)
        .build();
    String exportRequestBody = getValidJsonRequestForExport(exportConfiguration.getSelfUri(),
        searchResults.getResults());
    return restClient.post(exportUri, OrderItem.class, exportRequestBody);
  }

  @Override
  public OrderItem exportAndWaitForDownloadLink(SearchResults searchResults, ExportConfiguration exportConfiguration,
      String outputName, TimeUnit timeUnit, long timeOut) throws IOException {
    OrderItem result = export(searchResults, exportConfiguration, outputName);
    long maxWaitTime = System.currentTimeMillis() + timeUnit.toMillis(timeOut);
    while (result.getUri(LINK_DOWNLOAD) == null && System.currentTimeMillis() < maxWaitTime) {
      try {
        TimeUnit.SECONDS.sleep(2);
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
      result = restClient.refresh(result);
    }
    return result;
  }

  private String getValidJsonRequestForExport(String exportConfigurationUri,
      Collection<SearchResult> searchResults) {
    JsonNodeFactory jsonNodeFactory = new ObjectMapper().getNodeFactory();
    ObjectNode root = jsonNodeFactory.objectNode();
    root.set("exportConfiguration", jsonNodeFactory.textNode(exportConfigurationUri));
    root.set("includedRows", getIncludedRows(searchResults, jsonNodeFactory));
    return root.toString();
  }

  private ArrayNode getIncludedRows(Collection<SearchResult> searchResults,
      JsonNodeCreator jsonNodeCreator) {
    ArrayNode result = jsonNodeCreator.arrayNode();
    searchResults.stream()
        .map(SearchResult::getRows)
        .flatMap(Collection::stream)
        .map(Row::getId)
        .forEach(result::add);
    return result;
  }

  /**
   * Upload the transformation zip with the stylesheet into Archive.
   * @param exportTransformation The export transformation.
   * @param zip The input stream of zip with stylesheet.
   * @throws IOException When an I/O error occurs
   * @return The uploaded transformation
   * @deprecated Use declarative configuration to define transformations
   */
  @Override
  @Deprecated
  public LinkContainer uploadTransformation(ExportTransformation exportTransformation, InputStream zip)
      throws IOException {
    String uri = exportTransformation.getUri(LINK_EXPORT_TRANSFORMATION_ZIP);
    return restClient.post(uri, LinkContainer.class, new BinaryPart("file", zip, "stylesheet.zip"));
  }

  @Override public JobInstance search(String jobName, String applicationName, String tenantName)
      throws IOException {
    Services services = restClient.get(resourceCache.getServicesUri(), Services.class);

    JobDefinitions jobDefinitions = restClient.followNonPaged(services, InfoArchiveLinkRelations.LINK_JOB_DEFINITIONS, JobDefinitions.class);
    JobDefinition jobDefinition = jobDefinitions.byName(jobName);
    String runLink =  jobDefinition.getUri(InfoArchiveLinkRelations.LINK_JOB_INSTANCES);

    JobInstanceFilter jobSettings = new JobInstanceFilter();
    jobSettings.setNow(true);
    if (applicationName != null) {
      jobSettings.setApplication(applicationName);
    }
    if (tenantName != null) {
      jobSettings.setTenant(applicationName);
    }

    return restClient.post(runLink, JobInstance.class, jobSettings);
  }

}
