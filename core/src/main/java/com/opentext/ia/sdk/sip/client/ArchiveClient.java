/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client;

import java.io.IOException;
import java.io.InputStream;

import com.opentext.ia.sdk.sip.client.dto.OrderItem;
import com.opentext.ia.sdk.sip.client.dto.SearchComposition;
import com.opentext.ia.sdk.sip.client.dto.SearchResults;
import com.opentext.ia.sdk.sip.client.dto.export.ExportConfiguration;
import com.opentext.ia.sdk.sip.client.dto.export.ExportTransformation;
import com.opentext.ia.sdk.sip.client.dto.query.SearchQuery;
import com.opentext.ia.sdk.support.rest.LinkContainer;


/**
 * Client that interacts with an Archive.
 */
public interface ArchiveClient {

  /**
   * Ingest a Submission Information Package (SIP) into the Archive.
   * @param sip The SIP to add to the Archive
   * @return The ID of the Archival Information Package (AIP) that was generated from the SIP
   * @throws IOException When an I/O error occurs
   */
  String ingest(InputStream sip) throws IOException;

  /**
   * Ingest a Submission Information Package (SIP) into the Archive. Will take advantage of the ingestDirect resource if
   * present, otherwise will revert to receive + ingest.
   *
   * <b>Note</b>, only works if synchronous commit is enabled on the holding and the SIP being ingested is NOT part of a
   * multi-SIP DSS.
   * @param sip The SIP to add to the Archive
   * @return The ID of the Archival Information Package (AIP) that was generated from the SIP
   * @throws IOException When an I/O error occurs
   */
  String ingestDirect(InputStream sip) throws IOException;

  /**
   * Execute a query against the Archive.
   * @param query The query.
   * @param aic The name of the AIC.
   * @param schema The result set schema.
   * @param pageSize The page size of the result set.
   * @return A QueryResult
   * @throws IOException When an I/O error occurs
   */
  QueryResult query(SearchQuery query, String aic, String schema, int pageSize) throws IOException;

  /**
   * Fetch the content for the specified content id.
   * @param contentId The id of the content to fetch.
   * @return A ContentResult
   * @throws IOException When an I/O error occurs
   */
  ContentResult fetchContent(String contentId) throws IOException;

  /**
   * Fetch the content for the specified order item.
   * @param orderItem The order item.
   * @return A ContentResult
   * @throws IOException When an I/O error occurs
   */
  ContentResult fetchOrderContent(OrderItem orderItem) throws IOException;

  /**
   * Get the search results for the specified search query and composition.
   * @param searchQuery The search query.
   * @param searchComposition The search composition.
   * @return A SearchResults
   * @throws IOException When an I/O error occurs
   */
  SearchResults search(SearchQuery searchQuery, SearchComposition searchComposition) throws IOException;

  /**
   * Start the export of the search results for the specified export configuration.
   * @param searchResults The search results.
   * @param exportConfiguration The export configuration.
   * @param outputName The output name of result package.
   * @return An OrderItem object that contains information about exported package without link to download it
   * @throws IOException When an I/O error occurs
   */
  OrderItem export(SearchResults searchResults, ExportConfiguration exportConfiguration, String outputName) throws IOException;

  /**
   * Start and wait the export of the search results for the specified export configuration.
   * @param searchResults The search results.
   * @param exportConfiguration The export configuration.
   * @param outputName The output name of result package.
   * @param timeOutInMillis The timeout of export process in milliseconds.
   * @return An OrderItem object that contains information about exported package and link to download it
   * @throws IOException When an I/O error occurs
   */
  OrderItem exportAndWait(SearchResults searchResults, ExportConfiguration exportConfiguration, String outputName, long timeOutInMillis) throws IOException;

  /**
   * Upload the transformation zip with the stylesheet into the Archive.
   * @param exportTransformation The export transformation.
   * @param zip The input stream of zip with stylesheet.
   * @throws IOException When an I/O error occurs
   * @return The uploaded transformation
   */
  LinkContainer uploadTransformation(ExportTransformation exportTransformation, InputStream zip) throws IOException;

}
