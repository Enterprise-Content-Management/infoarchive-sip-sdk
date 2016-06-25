/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.InputStream;
import java.util.Map;

import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Link;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionRequest;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient {

  private static final String LINK_INGEST = "http://identifiers.emc.com/ingest";

  private final JsonFormatter formatter = new JsonFormatter();
  private final RestClient restClient = new RestClient(new HttpClient());
  private InfoArchiveConfiguration iaConfig;

  /**
   * Configure InfoArchive server with given configuration parameters.
   * @param configuration The parameters to configure InfoArchive server
   */
  @Override
  public void configure(Map<String, String> configuration) {
    iaConfig = new InfoArchiveConfigurationImpl(configuration, restClient);
  }

  /**
   * Ingests into InfoArchive server.
   * @param sip file to be ingested into InfoArchive server
   */
  @Override
  public String ingest(InputStream sip) {
    ReceptionResponse response = restClient.post(iaConfig.getAipsHref(), iaConfig.getHeaders(),
        formatter.format(new ReceptionRequest()), sip, ReceptionResponse.class);

    //TODO - report error if response fails

    Link ingestLink = response.getLinks().get(LINK_INGEST);
    IngestionResponse ingestionResponse = restClient.put(ingestLink.getHref(), iaConfig.getHeaders(),
        IngestionResponse.class);

    //TODO - Log ingestion response

    return ingestionResponse.getAipId();
  }

}
