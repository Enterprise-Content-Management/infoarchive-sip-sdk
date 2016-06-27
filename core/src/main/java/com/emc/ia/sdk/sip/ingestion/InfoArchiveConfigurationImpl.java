/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Applications;
import com.emc.ia.sdk.sip.ingestion.dto.Holding;
import com.emc.ia.sdk.sip.ingestion.dto.Holdings;
import com.emc.ia.sdk.sip.ingestion.dto.HomeResource;
import com.emc.ia.sdk.sip.ingestion.dto.Link;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;
import com.emc.ia.sdk.support.io.RuntimeIoException;


/**
 * Implementation of {@linkplain InfoArchiveConfiguration} that configures a running InfoArchive server.
 */
public class InfoArchiveConfigurationImpl implements InfoArchiveConfiguration {

  //TODO - are these URLs exposed anywhere through properties ?
  private static final String LINK_AIPS = "http://identifiers.emc.com/aips";
  private static final String LINK_TENANT = "http://identifiers.emc.com/tenant";
  private static final String LINK_APPLICATIONS = "http://identifiers.emc.com/applications";
  private static final String LINK_ADD = "http://identifiers.emc.com/add";
  private static final String LINK_HOLDINGS = "http://identifiers.emc.com/holdings";
  private static final String LINK_SELF = "self";

  private List<Header> headersJSON;
  private final RestClient restClient;
  private Tenant tenant;
  private Application application;
  private String aipsHref;

  public <T> InfoArchiveConfigurationImpl(Map<String, String> configuration, RestClient restClient) {
    //Map contains 3 keys ; "AuthToken" , "IAServer" , "Application" - Extract this information
    //TODO - Are these keys standardized somewhere ?
    //TODO - safety check, logging OR return ?
    this.restClient = restClient;
    setHeaders(configuration.get("AuthToken"));
    try {
      setTenant(configuration.get("IAServer"));
      setApplication(configuration.get("Application"), configuration.get("Holding"));
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
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

  private void setTenant(String resourceUrl) throws IOException {
    Objects.requireNonNull(resourceUrl, "IA server URL");
    HomeResource homeResource = restClient.get(resourceUrl, headersJSON, HomeResource.class);
    Link tenantLink = homeResource.getLinks().get(LINK_TENANT);
    Objects.requireNonNull(tenantLink, "Missing link to tenant in " + homeResource);
    tenant = restClient.get(tenantLink.getHref(), headersJSON, Tenant.class);
  }

  @Override
  public Application getApplication() {
    return application;
  }

  private void setApplication(String applicationName, String holdingName) throws IOException {
    Link applicationsLink = tenant.getLinks().get(LINK_APPLICATIONS);
    Objects.requireNonNull(applicationsLink, "Missing link to applications in " + tenant);
    Applications applications = restClient.get(applicationsLink.getHref(), headersJSON, Applications.class);
    application = applications.byName(applicationName);
    if (application == null) {
      addApplication(applications, applicationName, holdingName);
    }
  }

  private void addApplication(Applications applications, String applicationName, String holdingName)
      throws IOException {
    Link addLink = applications.getLinks().get(LINK_ADD);
    Objects.requireNonNull(addLink, "Missing link to add application in " + applications);
    restClient.post(addLink.getHref(), headersJSON, addApplicationBody(applicationName), null,
        Application.class);
    Link refreshLink = applications.getLinks().get(LINK_SELF);
    application = restClient.get(refreshLink.getHref(), headersJSON, Applications.class)
        .byName(applicationName);
    Objects.requireNonNull(application, "Could not find/create application " + applicationName);

    Link holdingsLink = application.getLinks().get(LINK_HOLDINGS);
    Objects.requireNonNull(holdingsLink, "Missing link to holdings");
    Holdings holdings = restClient.get(holdingsLink.getHref(), headersJSON, Holdings.class);
    addLink = holdings.getLinks().get(LINK_ADD);
    restClient.post(addLink.getHref(), headersJSON, addHoldingBody(holdingName), null, Holding.class);
  }

  private String addApplicationBody(String applicationName) {
    Application result = new Application();
    result.setName(applicationName);
    return new JsonFormatter().format(result);
  }

  private String addHoldingBody(String holdingName) {
    Holding result = new Holding();
    result.setName(holdingName);
    // TODO: Add PDI schema, indexes, etc.
    return new JsonFormatter().format(result);
  }

  @Override
  public String getAipsHref() {
    return aipsHref;
  }

  private void setAipsHref() {
    Link link = application.getLinks().get(LINK_AIPS);
    Objects.requireNonNull(link, "Missing link to AIPs in " + application);
    aipsHref = link.getHref();
  }

}
