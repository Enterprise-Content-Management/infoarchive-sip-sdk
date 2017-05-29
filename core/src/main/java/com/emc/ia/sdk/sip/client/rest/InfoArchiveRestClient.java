/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.http.client.utils.URIBuilder;

import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.ContentResult;
import com.emc.ia.sdk.sip.client.QueryResult;
import com.emc.ia.sdk.sip.client.dto.IngestionResponse;
import com.emc.ia.sdk.sip.client.dto.OrderItem;
import com.emc.ia.sdk.sip.client.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.client.dto.Row;
import com.emc.ia.sdk.sip.client.dto.SearchComposition;
import com.emc.ia.sdk.sip.client.dto.SearchDataBuilder;
import com.emc.ia.sdk.sip.client.dto.SearchResult;
import com.emc.ia.sdk.sip.client.dto.SearchResults;
import com.emc.ia.sdk.sip.client.dto.export.ExportConfiguration;
import com.emc.ia.sdk.sip.client.dto.export.ExportTransformation;
import com.emc.ia.sdk.sip.client.dto.query.Comparison;
import com.emc.ia.sdk.sip.client.dto.query.Item;
import com.emc.ia.sdk.sip.client.dto.query.QueryFormatter;
import com.emc.ia.sdk.sip.client.dto.query.SearchQuery;
import com.emc.ia.sdk.support.http.BinaryPart;
import com.emc.ia.sdk.support.http.MediaTypes;
import com.emc.ia.sdk.support.http.ResponseFactory;
import com.emc.ia.sdk.support.http.TextPart;
import com.emc.ia.sdk.support.rest.LinkContainer;
import com.emc.ia.sdk.support.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient, InfoArchiveLinkRelations {

  private final ResponseFactory<DefaultQueryResult> queryResultFactory = new QueryResultFactory();
  private final ResponseFactory<ContentResult> contentResultFactory = new ContentResultFactory();
  private final QueryFormatter queryFormatter = new QueryFormatter();

  private final RestClient restClient;
  private final ArchiveOperationsByApplicationResourceCache resourceCache;

  public InfoArchiveRestClient(RestClient restClient, ArchiveOperationsByApplicationResourceCache resourceCache) {
    this.restClient = restClient;
    this.resourceCache = resourceCache;
  }

  @Override
  public String ingest(InputStream sip) throws IOException {
    ReceptionResponse response = restClient.post(resourceCache.getAipResourceUri(), ReceptionResponse.class,
        new TextPart("format", "sip_zip"), new BinaryPart("sip", sip, "IASIP.zip"));
    return restClient.post(response.getUri(LINK_INGEST), IngestionResponse.class).getAipId();
  }

  @Override
  public String ingestDirect(InputStream sip) throws IOException {
    String ingestDirectUri = resourceCache.getAipIngestDirectResourceUri();
    if (ingestDirectUri == null) {
      return ingest(sip);
    } else {
      return restClient.post(ingestDirectUri, IngestionResponse.class, new TextPart("format", "sip_zip"),
          new BinaryPart("sip", sip, "IASIP.zip")).getAipId();
    }
  }

  @Override
  public QueryResult query(SearchQuery query, String aic, String schema, int pageSize) throws IOException {
    String formattedQuery = queryFormatter.format(query);
    String baseUri = resourceCache.getDipResourceUriByAicName().get(aic);
    Objects.requireNonNull(baseUri, String.format("No DIP resource found for AIC %s", aic));
    String queryUri = restClient.uri(baseUri).addParameter("query", formattedQuery).addParameter("schema", schema)
        .addParameter("size", String.valueOf(pageSize)).build();
    return restClient.get(queryUri, queryResultFactory);
  }

  @Override
  public ContentResult fetchContent(String contentId) throws IOException {
    try {
      String contentResource = resourceCache.getCiResourceUri();
      URIBuilder builder = new URIBuilder(contentResource);
      builder.setParameter("cid", contentId);
      URI uri = builder.build();
      return restClient.get(uri.toString(), contentResultFactory);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Failed to create content resource uri.", e);
    }
  }

  @Override
  public ContentResult fetchOrderContent(OrderItem orderItem) throws IOException {
    String fetchUri = restClient.uri(orderItem.getUri(LINK_DOWNLOAD))
      .addParameter("downloadToken", "")
      .build();
    return restClient.get(fetchUri, contentResultFactory);
  }

  @Override
  public SearchResults search(SearchQuery searchQuery, SearchComposition searchComposition) throws IOException {
    String searchResultBaseUri = searchComposition.getSelfUri();
    String xmlSearchQuery = getXmlStringFromSearchQuery(searchQuery);
    SearchResults result = restClient.post(searchResultBaseUri, SearchResults.class, xmlSearchQuery, MediaTypes.XML);
    while (searchResultBaseUri != null) {
      SearchResults onePageSearchResults = restClient.post(searchResultBaseUri, SearchResults.class, "",
          MediaTypes.XML);
      for (SearchResult searchResult: onePageSearchResults.getResults()) {
        result.addResult(searchResult);
      }
      searchResultBaseUri = onePageSearchResults.getUri("next");
    }
    return result;
  }

  private String getXmlStringFromSearchQuery(SearchQuery searchQuery) {
    SearchDataBuilder searchDataBuilder = SearchDataBuilder.builder();
    for (Item item: searchQuery.getItems()) {
      if (item instanceof Comparison) {
        Comparison comparison = (Comparison)item;
        switch (comparison.getOperator()) {
          case EQUAL: searchDataBuilder.equal(comparison.getName(), comparison.getValue().get(0));
            break;
          case NOT_EQUAL:
            searchDataBuilder.notEqual(comparison.getName(), comparison.getValue().get(0));
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
    }
    return searchDataBuilder.build().replaceFirst("<\\?.*\\?>", "").replace(" ", "").replace("\n", "");
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
  public OrderItem exportAndWait(SearchResults searchResults, ExportConfiguration exportConfiguration,
      String outputName, long timeOutInMillis) throws IOException {
    String fullOutputName = outputName + '_' + Long.toString(new Date().getTime());
    String exportUri = restClient.uri(searchResults.getUri(LINK_EXPORT))
        .addParameter("name", fullOutputName)
        .build();
    String exportRequestBody = getValidJsonRequestForExport(exportConfiguration.getSelfUri(),
        searchResults.getResults());
    OrderItem plainOrderItem = restClient.post(exportUri, OrderItem.class, exportRequestBody);

    long endTimeOfExport;
    if (timeOutInMillis < 5000) {
      endTimeOfExport = System.currentTimeMillis() + 5000;
    } else {
      endTimeOfExport = System.currentTimeMillis() + timeOutInMillis;
    }
    while (System.currentTimeMillis() < endTimeOfExport) {
      OrderItem downloadOrderItem = restClient.get(plainOrderItem.getSelfUri(), OrderItem.class);
      if (downloadOrderItem.getUri(LINK_DOWNLOAD) != null) {
        return downloadOrderItem;
      }
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        // Ignored
      }
    }
    return plainOrderItem;
  }

  private String getValidJsonRequestForExport(String exportConfigurationUri, List<SearchResult> searchResults)
      throws IOException {
    JsonNodeFactory jsonNodeFactory = new ObjectMapper().getNodeFactory();
    ObjectNode root = jsonNodeFactory.objectNode();
    ArrayNode includedRows = jsonNodeFactory.arrayNode();
    for (SearchResult searchResult: searchResults) {
      for (Row row: searchResult.getRows()) {
        includedRows.add(row.getId());
      }
    }
    TextNode exportConfiguration = jsonNodeFactory.textNode(exportConfigurationUri);
    root.set("exportConfiguration", exportConfiguration);
    root.set("includedRows", includedRows);
    return root.toString();
  }

  @Override
  public LinkContainer uploadTransformation(ExportTransformation exportTransformation, InputStream zip) throws IOException {
    String uri = exportTransformation.getUri(LINK_EXPORT_TRANSFORMATION_ZIP);
    return restClient.post(uri, LinkContainer.class, new BinaryPart("file", zip, "stylesheet.zip"));
  }

}
