/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Applications;
import com.emc.ia.sdk.sip.ingestion.dto.HomeResource;
import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Link;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionRequest;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;


/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient {

  public static final String AUTH_TOKEN = "AuthToken";
  public static final String HOME_RESOURCE = "IAServer";
  public static final String APPLICATION_NAME = "Application";
  //TODO - are these URLs exposed anywhere through properties ?
  private static final String LINK_INGEST = "http://identifiers.emc.com/ingest";
  private static final String LINK_AIPS = "http://identifiers.emc.com/aips";
  private static final String LINK_TENANT = "http://identifiers.emc.com/tenant";
  private static final String LINK_APPLICATION = "http://identifiers.emc.com/applications";
  private final List<Header> headers = new ArrayList<Header>();
  private final JsonFormatter formatter = new JsonFormatter();
  private boolean isConfigInvoked;
  private Tenant tenant;
  private Application application;
  private String aipsHref;
  private RestClient restClient = new RestClient(new HttpClient());

  /**
   * Configure InfoArchive server with given configuration parameters.
   * @param configuration The parameters to configure InfoArchive server
   */
  @Override
  public void configure(Map<String, String> configuration) {
    //Map contains 3 keys ; "AuthToken" , "IAServer" , "Application" - Extract this information
    //TODO - Are these keys standardized somewhere ?    
    setHeaders(Objects.requireNonNull(configuration.get(AUTH_TOKEN)));
    setTenant(Objects.requireNonNull(configuration.get(HOME_RESOURCE)));
    setApplication(Objects.requireNonNull(configuration.get(APPLICATION_NAME)));
    setAipsHref();
    isConfigInvoked = true;
  }

  /**
   * Ingests into InfoArchive server.
   * @param sip file to be ingested into InfoArchive server
   */
  @Override
  public String ingest(InputStream sip) {
    if (!isConfigInvoked) {
      throw new RuntimeException("Confiration is not invoked on ArchiveClient");
    }
    ReceptionResponse response = null;
    try {
      response = restClient.post(aipsHref, headers,
          formatter.format(new ReceptionRequest()), Objects.requireNonNull(sip), ReceptionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Link ingestLink = response.getLinks().get(LINK_INGEST);
    IngestionResponse ingestionResponse = null;
    try {
      ingestionResponse = restClient.put(ingestLink.getHref(), headers,
          IngestionResponse.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return ingestionResponse.getAipId();
  }

  private void setHeaders(String authToken) {
    headers.add(new BasicHeader("AuthToken", authToken));
    headers.add(new BasicHeader("Accept", "application/hal+json"));
  }

  private void setTenant(String resourceUrl) {
    HomeResource homeResource = null;
    try {
      homeResource = restClient.get(resourceUrl, headers, HomeResource.class);
      Link tenantLink = homeResource.getLinks().get(LINK_TENANT);
      tenant = restClient.get(tenantLink.getHref(), headers, Tenant.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void setApplication(String applicationName) {
    Link applicationsLink = tenant.getLinks().get(LINK_APPLICATION);
    Applications applications = null;
    try {
      applications = restClient.get(applicationsLink.getHref(), headers, Applications.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    application = applications.byName(applicationName);
  }

  private void setAipsHref() {
    Link aipsLink = application.getLinks().get(LINK_AIPS);
    aipsHref = aipsLink.getHref();
  }

  public void setRestClient(RestClient restClient) {
    this.restClient = restClient;
  }
}
