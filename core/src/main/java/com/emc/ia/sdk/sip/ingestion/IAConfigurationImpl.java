/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */

package com.emc.ia.sdk.sip.ingestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;


/**
 * Implementation of {@linkplain IAConfiguration} that configures a running InfoArchive server.
 */
public class IAConfigurationImpl implements IAConfiguration {

  //TODO - are these URLs exposed anywhere through properties ?
  private static final String LINK_AIPS = "http://identifiers.emc.com/aips";
  private static final String LINK_TENANT = "http://identifiers.emc.com/tenant";
  private static final String LINK_APPLICATION = "http://identifiers.emc.com/applications";

  private List<Header> headersJSON;
  private final SimpleRestClient restClient;
  private Tenant tenant;
  private Application application;
  private String aipsHref;

  public <T> IAConfigurationImpl(Map<String, String> configuration, SimpleRestClient restClient) {
    //Map contains 3 keys ; "AuthToken" , "IAServer" , "Application" - Extract this information
    //TODO - Are these keys standardized somewhere ?
    //TODO - safety check, logging OR return ?
    this.restClient = restClient;
    setHeaders(configuration.get("AuthToken"));
    setTenant(configuration.get("IAServer"));
    setApplication(configuration.get("Application"));
    setAipsHref();
  }

  @Override
  public List<Header> getHeaders() {
    return headersJSON;
  }

  private void setHeaders(String authToken) {
    headersJSON = new ArrayList<Header>();
    headersJSON.add(new BasicHeader("AuthToken", authToken));
    headersJSON.add(new BasicHeader("Accept", "application/hal+json"));
  }

  @Override
  public Tenant getTenant() {
    return tenant;
  }

  private void setTenant(String resourceUrl) {
    IAHomeResource homeResource = restClient.get(resourceUrl, headersJSON, IAHomeResource.class);
    Link tenantLink = homeResource.getLinks().get(LINK_TENANT);
    tenant = restClient.get(tenantLink.getHref(), headersJSON, Tenant.class);
  }

  @Override
  public Application getApplication() {
    return application;
  }

  private void setApplication(String applicationName) {
    Link applicationsLink = tenant.getLinks().get(LINK_APPLICATION);
    Applications applications = restClient.get(applicationsLink.getHref(), headersJSON, Applications.class);
    application = applications.byName(applicationName);
  }

  @Override
  public String getAipsHref() {
    return aipsHref;
  }

  private void setAipsHref() {
    Link aipsLink = application.getLinks().get(LINK_AIPS);
    aipsHref = aipsLink.getHref();
  }

}
