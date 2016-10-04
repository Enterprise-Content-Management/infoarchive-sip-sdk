/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.emc.ia.sdk.sip.client.dto.query.SearchQuery;

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
   * Fetch the exported package for the specified URI, file name and download token.
   * @param baseUri The URI of the package to fetch.
   * @param fileName The file name of the package.
   * @param downloadToken The download token to fetch.
   * @return A ContentResult
   * @throws IOException When an I/O error occurs
   */
  ContentResult fetchExportedPackage(URI baseUri, String fileName, String downloadToken) throws IOException;

}
