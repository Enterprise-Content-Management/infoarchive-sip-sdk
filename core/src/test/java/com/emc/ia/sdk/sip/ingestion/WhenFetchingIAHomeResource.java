/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class WhenFetchingIAHomeResource {

  private final TestHomeResource app = new TestHomeResource();
  
  @Before
  public void init() {
    app.setName("TestApplication");
  }
  
  @Test
  public void fetchResourceName() {    
    assertEquals("Resource Name", app.getName(), "TestApplication");
  }
    
  @Test
  public void validateLinkToStringValue() {        
    assertEquals("Resource ToString check", app.toString(), "IAHomeResource [name=" + "TestApplication" + ", links=" + app.getLinks() + " ]");
  }
    
  public static class TestHomeResource extends IAHomeResource {    
    
     public Map<String, Link> getLinks() {
        
        String linkStr = "https://india.emc.com/content-management/infoarchive/infoarchive.htm";
        Link link = new Link(); 
        Map<String, Link> links = new HashMap<String, Link>();
        
        link.setHref(linkStr); 
        links.put("testApp", link);
        
        return links;
    }
    
  }
  
}
