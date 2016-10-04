/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.apache.http.client.utils.URIBuilder;

import com.emc.ia.sdk.configurer.InfoArchiveConfiguration;
import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.ContentResult;
import com.emc.ia.sdk.sip.client.QueryResult;
import com.emc.ia.sdk.sip.client.dto.IngestionResponse;
import com.emc.ia.sdk.sip.client.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.client.dto.query.QueryFormatter;
import com.emc.ia.sdk.sip.client.dto.query.SearchQuery;
import com.emc.ia.sdk.support.http.BinaryPart;
import com.emc.ia.sdk.support.http.ResponseFactory;
import com.emc.ia.sdk.support.http.TextPart;
import com.emc.ia.sdk.support.rest.RestClient;

/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient, InfoArchiveLinkRelations, InfoArchiveConfiguration {

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
    return restClient.put(response.getUri(LINK_INGEST), IngestionResponse.class)
      .getAipId();
  }

  @Override
  public QueryResult query(SearchQuery query, String aic, String schema, int pageSize) throws IOException {
    String formattedQuery = queryFormatter.format(query);
    String baseUri = resourceCache.getDipResourceUriByAicName()
      .get(aic);
    Objects.requireNonNull(baseUri, String.format("No DIP resource found for AIC %s", aic));
    String queryUri = restClient.uri(baseUri)
      .addParameter("query", formattedQuery)
      .addParameter("schema", schema)
      .addParameter("size", String.valueOf(pageSize))
      .build();
    return restClient.get(queryUri, queryResultFactory);
  }

  @Override
  public ContentResult fetchContent(String contentId) throws IOException {
    try {
      String contentResource = resourceCache.getCiResourceUri();
      final URIBuilder builder = new URIBuilder(contentResource);
      builder.setParameter("cid", contentId);
      final URI uri = builder.build();
      return restClient.get(uri.toString(), contentResultFactory);
    } catch (URISyntaxException e) {
      throw new RuntimeException("Failed to create content resource uri.", e);
    }
  }

  @Override
  public ContentResult fetchExportedPackage(URI baseUri, String fileName, String downloadToken) throws IOException {
    String queryUri = restClient.uri(baseUri.toString())
      .addParameter("filename", fileName.replace(" ", "%20"))
      .addParameter("downloadToken", downloadToken)
      .build();
    return restClient.get(queryUri, contentResultFactory);
  }

}
