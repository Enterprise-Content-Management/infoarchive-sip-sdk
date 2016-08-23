/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.emc.ia.sdk.sip.ingestion.dto.SearchResults;
import com.emc.ia.sdk.sip.ingestion.dto.query.QueryResult;
import com.emc.ia.sdk.sip.ingestion.dto.query.SearchQuery;

/**
 * Client that interacts with an Archive.
 */
public interface ArchiveClient {

  /**
   * Configure the Archive client with options.
   * @param configuration Options that change the behavior of the client
   */
  void configure(Map<String, String> configuration);

  /**
   * Ingest a Submission Information Package (SIP) into the Archive.
   * @param sip The SIP to add to the Archive
   * @return The ID of the Archival Information Package (AIP) that was generated from the SIP
   * @throws IOException When an I/O error occurs
   */
  String ingest(InputStream sip) throws IOException;

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
   * Execute a search against the Archive.
   * @param searchName The name of the search.
   * @param searchData The search data xml.
   * @param options The search options.
   * @return A SearchResult
   * @throws IOException When an I/O error occurs
   */
  SearchResults search(String searchName, String searchData, SearchOptions options) throws IOException;
}
