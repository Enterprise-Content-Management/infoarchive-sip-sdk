/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.junit.Before;
import org.junit.Test;

public class WhenInitializingArchiveLinkConfigurationParameters {

  private final Map<String, String> configuration = new HashMap<String, String>();
  public static final String TESTSTRING = "http://identifiers.emc.com/tenant";
  private IAConfiguration config;
  
  @Before
  public void init() {
    configuration.put("AuthToken","XYZ123ABC");
    configuration.put("Application","Test");
    configuration.put("IAServer","Test");
    config = new IAConfigurationImpl(configuration, new TestRestClient());
  } 
  
  @Test
  public void shouldInitHeadersDuringObjectCreation() {    
    assertEquals("Headers 'AuthToken' information", config.getHeaders().get(0).toString(), "AuthToken: XYZ123ABC");
    assertEquals("Headers 'Accept' information", config.getHeaders().get(1).toString(), "Accept: application/hal+json");    
  }  
  
  @Test 
  public void shouldInitTenantDuringObjectCreation() {    
    assertNotNull(config.getTenant());
  }
  
  @Test 
  public void shouldInitApplicationDuringObjectCreation() {    
    assertNotNull(config.getApplication());
  }
  
  @Test
  public void shouldInitApisHrefDuringObjectCreation() {     
    assertEquals("Headers ApisHref information", config.getAipsHref(), TESTSTRING);   
  }
  
   public static class TestRestClient extends SimpleRestClient{
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String uri, List<Header> headers, final Class<T> type) {
     
      T result = null;      
      if(type.getName().equals("com.emc.ia.sdk.sip.ingestion.Tenant")) {
        result = (T)new TestTenant();
      }
      else if(type.getName().equals("com.emc.ia.sdk.sip.ingestion.IAHomeResource")) {
        result = (T)new TestResource();
      }
      else if(type.getName().equals("com.emc.ia.sdk.sip.ingestion.Applications")) {
        result = (T)new TestApplications();
      }
      
      return result;
    }
     
     public static class TestResource extends IAHomeResource{
       
       private final Map<String, Link> links = new HashMap<String, Link>();
       
       @Override
       public Map<String, Link> getLinks() {
         Link link = new Link();
         link.setHref(TESTSTRING);
         links.put(TESTSTRING, link);
         return links;
       }      
   
     }    
  }
   
   public static class TestTenant extends Tenant{
     
     private final Map<String, Link> links = new HashMap<String, Link>();
     
     @Override
     public Map<String, Link> getLinks() {
       Link link = new Link();
       link.setHref(TESTSTRING);
       links.put("http://identifiers.emc.com/applications", link);
       return links;
     }      
 
   }
   
   public static class TestApplications extends Applications {
     
     private final Map<String, Link> links = new HashMap<String, Link>();
     
     @Override
     public Application byName(String name) {
       Application app = new Application();
       Link link = new Link();
       link.setHref(TESTSTRING);
       links.put("http://identifiers.emc.com/aips", link);
       app.setLinks(links);
       return app;
     }
     
   }
   
  
}
