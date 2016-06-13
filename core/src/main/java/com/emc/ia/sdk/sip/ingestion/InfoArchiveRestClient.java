/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.InputStream;
import java.util.Map;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient {

  @Override
  public void configure(Map<String, String> configuration) {
    // TODO: Extract location of IA server
  }

  @Override
  public String ingest(InputStream sip) {
    throw new IllegalStateException("Not yet implemented");
  }

}
