/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

<<<<<<< HEAD
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
=======
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
>>>>>>> branch 'master' of https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk.git

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
  private static final String TEST_HREF= "test";
  
  private final Map<String, Link> links = new HashMap<String, Link>();  
  private final Map<String, String> configuration = new HashMap<String, String>();  
  private static final String URI = "http://test.ia.emc.com";
  private InfoArchiveRestClient client = new InfoArchiveRestClient();
  private GenericRestClient restClient;
  private HomeResource resource;
  private Link tenantLink;
  private Tenant tenant;
  private Application application;
  private Applications applications;
  
  @Before
  public void init() {
    configuration.put("AuthToken", "XYZ123ABC");
    configuration.put("Application", "Test");
<<<<<<< HEAD
    configuration.put("IAServer", URI);    
    restClient = mock(GenericRestClient.class);
    resource = mock(HomeResource.class);
    tenantLink = mock(Link.class);
    tenant = mock(Tenant.class); 
    application = mock(Application.class);
    applications = mock(Applications.class);
    client.setRestClient(restClient);
        
    links.put(LINK_TENANT, tenantLink);
    links.put(LINK_APPLICATION, tenantLink);
    links.put(LINK_AIPS, tenantLink);
    links.put(LINK_INGEST, tenantLink);
    
    when(restClient.get(eq(URI), any(JsonHeaders.class), eq(HomeResource.class))).thenReturn(resource);
    when(resource.getLinks()).thenReturn(links);
    when(tenantLink.getHref()).thenReturn("test");
    when(restClient.get(eq(TEST_HREF), any(JsonHeaders.class), eq(Tenant.class))).thenReturn(tenant);
    when(tenant.getLinks()).thenReturn(links);
    when(restClient.get(eq(TEST_HREF), any(JsonHeaders.class), eq(Applications.class))).thenReturn(applications);
    when(applications.byName("Test")).thenReturn(application);
    when(application.getLinks()).thenReturn(links);
=======
    configuration.put("IAServer", "Test");
    HttpClient client = mock(HttpClient.class);
    config = new InfoArchiveConfigurationImpl(configuration, new TestRestClient(client));
>>>>>>> branch 'master' of https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk.git
  }
  
  @Test
  public void shouldInitHeadersDuringObjectCreation() {
    
    client.configure(configuration);
    
    verify(restClient).get(eq(URI), any(JsonHeaders.class), eq(HomeResource.class));
    verify(restClient).get(eq(TEST_HREF), any(JsonHeaders.class), eq(Tenant.class));
    verify(restClient).get(eq(TEST_HREF), any(JsonHeaders.class), eq(Applications.class));
    verify(applications).byName("Test");
    verify(tenantLink, times(3)).getHref();    
  }

  
  @Test
  public void shouldIngestSuccessfully() throws IOException {    
    client.configure(configuration);
    
    String source = "This is the source of my input stream";
    InputStream sip = IOUtils.toInputStream(source, "UTF-8");
    
    ReceptionResponse receptionResponse = mock(ReceptionResponse.class);
    IngestionResponse ingestionResponse = mock(IngestionResponse.class);
    when(restClient.post(anyString(), any(JsonHeaders.class), anyString(), eq(sip), eq(ReceptionResponse.class))).thenReturn(receptionResponse);
    when(receptionResponse.getLinks()).thenReturn(links);
    when(restClient.put(eq(TEST_HREF), any(JsonHeaders.class), eq(IngestionResponse.class))).thenReturn(ingestionResponse);
    when(ingestionResponse.getAipId()).thenReturn("sip001");
    
    assertEquals(client.ingest(sip), "sip001");
    
    verify(restClient).post(eq(TEST_HREF), any(JsonHeaders.class), anyString(), eq(sip), eq(ReceptionResponse.class));
    verify(restClient).put(eq(TEST_HREF), any(JsonHeaders.class), eq(IngestionResponse.class));
    verify(tenantLink, times(4)).getHref();
    verify(receptionResponse).getLinks();
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
<<<<<<< HEAD
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionWhenConfigurationParametersAreNull() throws IOException {
    Map<String, String> config = new HashMap<String, String>();    
    client.configure(config);    
=======

  public static class TestRestClient extends RestClient {

    public TestRestClient(HttpClient client) {
      super(client);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String uri, List<Header> headers, Class<T> type) {
      T result = null;
      if (type.getName().equals(Tenant.class.getName())) {
        result = (T)new TestTenant();
      } else if (type.getName().equals(HomeResource.class.getName())) {
        result = (T)new TestResource();
      } else if (type.getName().equals(Applications.class.getName())) {
        result = (T)new TestApplications();
      }
      return result;
    }


    public static class TestResource extends HomeResource {

      private final Map<String, Link> links = new HashMap<String, Link>();

      @Override
      public Map<String, Link> getLinks() {
        Link link = new Link();
        link.setHref(TESTSTRING);
        links.put(TESTSTRING, link);
        return links;
      }

    }
>>>>>>> branch 'master' of https://github.com/Enterprise-Content-Management/infoarchive-sip-sdk.git
  }
}