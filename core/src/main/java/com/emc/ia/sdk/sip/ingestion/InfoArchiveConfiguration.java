/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.util.List;

import org.apache.http.Header;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;


/**
 * Stores InfoArchive server configuration parameters.
 */
public interface InfoArchiveConfiguration {

  Tenant getTenant();

  Application getApplication();

  // TODO: Remove this when test no longer calls it
  List<Header> getHeaders();

  String getAipsHref();

}
