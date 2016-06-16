/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.emc.ia.sdk.sip.ingestion.ArchiveClient;

/**
 * Implementation of {@linkplain ArchiveClient} that uses the REST API of a running InfoArchive server.
 */
public class InfoArchiveRestClient implements ArchiveClient {

  //TODO - are these URLs exposed anywhere through properties ?
  private static final String LINK_AIPS = "http://identifiers.emc.com/aips";
  private static final String LINK_INGEST = "http://identifiers.emc.com/ingest";
  private static final String LINK_TENANT = "http://identifiers.emc.com/tenant";
  private static final String LINK_APPLICATION = "http://identifiers.emc.com/applications";
  
  private final List<Header> headersJSON;
  private final JSONFormatter formatterJSON;
  private final SimpleRestClient restClient;
  private String aipsHref;
     
  public InfoArchiveRestClient() {
    
    restClient = new SimpleRestClient();
    headersJSON = new ArrayList<Header>();
    formatterJSON = new JSONFormatter();    
  }
  
  @Override
  public void configure(Map<String, String> configuration) {  
    
    //Map contains 3 keys ; "AuthToken" , "IAServer" , "Application" - Extract this information
    //TODO - Are these keys standardized somewhere ?
    
    //TODO - safety check, logging OR return ?   
    
    headersJSON.add(new BasicHeader("AuthToken", configuration.get("AuthToken")));
    headersJSON.add(new BasicHeader("Accept", "application/hal+json"));
    
    init(configuration.get("IAServer"), configuration.get("Application"), configuration.get("AuthToken"));
  }

  private void init(String resourceUrl, String applicationName, String token) {

    IAHomeResource homeResource = restClient.get(resourceUrl, headersJSON, IAHomeResource.class);
    Tenant tenant = getTenant(homeResource);
    Application application = getApplication(tenant, applicationName);

    Link aipsLink = application.getLinks().get(LINK_AIPS);
    aipsHref = aipsLink.getHref();
  }

  private Tenant getTenant(IAHomeResource homeResource) {
    Link tenantLink = homeResource.getLinks().get(LINK_TENANT);
    Tenant tenant = restClient.get(tenantLink.getHref(), headersJSON, Tenant.class);
    return tenant;
  }
  
  private Application getApplication(Tenant tenant, String name) {
    Link applicationsLink = tenant.getLinks().get(LINK_APPLICATION);
    Applications applications = restClient.get(applicationsLink.getHref(), headersJSON, Applications.class);
    Application application = applications.byName(name);
    return application;
  }
  
  @Override
  public String ingest(InputStream sip) {    
    
    ReceptionResponse response = restClient.post(aipsHref, headersJSON, formatterJSON.format(new Reception()), sip, ReceptionResponse.class);

    //TODO - report error if response fails

    Link ingestLink = response.getLinks().get(LINK_INGEST);

    IngestionResponse ingestionResponse = restClient.put(ingestLink.getHref(), headersJSON, IngestionResponse.class);

    //TODO - Log ingestion response
    
    return ingestionResponse.getAipId();    
  } 
  
}
