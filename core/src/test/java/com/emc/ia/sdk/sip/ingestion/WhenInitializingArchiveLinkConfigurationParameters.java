/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.sip.ingestion.dto.Application;
import com.emc.ia.sdk.sip.ingestion.dto.Applications;
import com.emc.ia.sdk.sip.ingestion.dto.HomeResource;
import com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Link;
import com.emc.ia.sdk.sip.ingestion.dto.ReceptionResponse;
import com.emc.ia.sdk.sip.ingestion.dto.Tenant;


public class WhenInitializingArchiveLinkConfigurationParameters {
  private static final String LINK_TENANT = "http://identifiers.emc.com/tenant";
  private static final String LINK_APPLICATION = "http://identifiers.emc.com/applications";
  private static final String LINK_AIPS = "http://identifiers.emc.com/aips";
  private static final String LINK_INGEST = "http://identifiers.emc.com/ingest";
  private static final String TEST_HREF = "Test";
  private final Map<String, Link> links = new HashMap<String, Link>();
  private final Map<String, String> configuration = new HashMap<String, String>();
  private final InfoArchiveRestClient client = new InfoArchiveRestClient();
  private RestClient restClient;
  private Applications applications;

  @Before
  public void init() throws IOException {
    configuration.put("AuthToken", "XYZ123ABC");
    configuration.put("Application", TEST_HREF);
    configuration.put("IAServer", TEST_HREF);
    restClient = mock(RestClient.class);
    HomeResource resource = new HomeResource();
    Link tenantLink = mock(Link.class);
    Tenant tenant = new Tenant();
    Application application = new Application();
    applications = mock(Applications.class);
    client.setRestClient(restClient);
    links.put(LINK_TENANT, tenantLink);
    links.put(LINK_APPLICATION, tenantLink);
    links.put(LINK_AIPS, tenantLink);
    links.put(LINK_INGEST, tenantLink);
    resource.setLinks(links);
    tenant.setLinks(links);
    application.setLinks(links);
    when(restClient.get(eq(TEST_HREF), anyObject(), eq(HomeResource.class))).thenReturn(resource);
    when(tenantLink.getHref()).thenReturn(TEST_HREF);
    when(restClient.get(eq(TEST_HREF), anyObject(), eq(Tenant.class))).thenReturn(tenant);
    when(restClient.get(eq(TEST_HREF), anyObject(), eq(Applications.class))).thenReturn(applications);
    when(applications.byName(TEST_HREF)).thenReturn(application);
    configuration.put("IAServer", TEST_HREF);
  }

  @Test
  public void shouldInitHeadersDuringObjectCreation() throws IOException {
    client.configure(configuration);
    verify(restClient).get(eq(TEST_HREF), anyObject(), eq(HomeResource.class));
    verify(restClient).get(eq(TEST_HREF), anyObject(), eq(Tenant.class));
    verify(restClient).get(eq(TEST_HREF), anyObject(), eq(Applications.class));
    verify(applications).byName(TEST_HREF);
  }

  @Test (expected = RuntimeException.class)
  public void shouldThrowExceptionWileConfiguring() {
    client.configure(null);
  }

  @SuppressWarnings("unchecked")
  @Test (expected = RuntimeException.class)
  public void shouldThrowExceptionWileSettingTenent() throws IOException {
    when(restClient.get(eq(TEST_HREF), anyObject(), eq(Tenant.class))).thenThrow(IOException.class);
    client.configure(configuration);
  }

  @SuppressWarnings("unchecked")
  @Test (expected = RuntimeException.class)
  public void shouldThrowExceptionWileSettingApplication() throws IOException {
    when(restClient.get(eq(TEST_HREF), anyObject(), eq(Applications.class))).thenThrow(IOException.class);
    client.configure(configuration);
  }

  @Test
  public void shouldIngestSuccessfully() throws IOException {
    client.configure(configuration);

    String source = "This is the source of my input stream";
    InputStream sip = IOUtils.toInputStream(source, "UTF-8");

    ReceptionResponse receptionResponse = new ReceptionResponse();
    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    receptionResponse.setLinks(links);
    when(restClient.post(anyString(), anyObject(), anyString(), eq(sip), eq(ReceptionResponse.class))).thenReturn(receptionResponse);
    when(restClient.put(eq(TEST_HREF), anyObject(), eq(IngestionResponse.class))).thenReturn(ingestionResponse);
    when(ingestionResponse.getAipId()).thenReturn("sip001");

    String aipid = client.ingest(sip);
    assertEquals(aipid, "sip001");

    verify(restClient).post(eq(TEST_HREF), anyObject(), anyString(), eq(sip), eq(ReceptionResponse.class));
    verify(restClient).put(eq(TEST_HREF), anyObject(), eq(IngestionResponse.class));
  }

  @Test(expected = RuntimeException.class)
  public void ingestShouldThrowRuntimeExceptionWhenConfigureIsNotInvoked() throws IOException {
    String source = "This is the source of my input stream";
    InputStream sip = IOUtils.toInputStream(source, "UTF-8");
    client.ingest(sip);
  }

  @Test(expected = RuntimeException.class)
  public void ingestShouldThrowRuntimeExceptionWhenSipIsNull() throws IOException {
    client.ingest(null);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionWhenConfigurationParametersAreNull() throws IOException {
    Map<String, String> config = new HashMap<String, String>();
    client.configure(config);
  }
}
