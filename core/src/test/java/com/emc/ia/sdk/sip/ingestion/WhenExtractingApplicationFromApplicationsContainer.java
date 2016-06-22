/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class WhenExtractingApplicationFromApplicationsContainer {
 
  private final TestApplication app = new TestApplication();
  private final Applications apps = new Applications();
  
  @Before
  public void init()
  {
    app.setName("TestApplication");
  }
  
  @Test
  public void fetchApplicationName() {    
    
    assertEquals("Application Name", app.getName(), "TestApplication");
  }
    
  @Test
  public void validateLinkToStringValue() {
        
    assertEquals("Application ToString check", app.toString(), "Application [name=" + "TestApplication" + ", getLinks()=" + app.getLinks() + " ]");
  }
  
  @Test
  public void shouldReturnValidApplicationObjectWithDefaultKeyAndPreSetName() {
    prepare("applications");
    assertNotNull(apps.byName("TestApp"));    
  }
  
  @Test
  public void shouldReturnNullWhenQueriedForApplicationWithWrongName() {
    prepare("applications");
    assertNull(apps.byName("Not_A_Valid_Name"));    
  }
  
  @Test
  public void shouldReturnNullWhenQueriedForApplicationWithWrongKeyButPreSetName() {
    prepare("Not_a_valid_key");
    assertNull(apps.byName("TestApp"));    
  }
  
  @Test
  public void shouldReturnNullWhenQueriedForApplicationWithWrongKeyAndWrongName() {
    prepare("Not_a_valid_key");
    assertNull(apps.byName("Not_A_Valid_Name"));    
  }
  
  private void prepare(String key) {
   
    Map<String,List<Application>> embedded = new HashMap<String,List<Application>>();
    List<Application> list = new ArrayList<Application>();
    Application app = new Application();
    app.setName("TestApp");
    list.add(app);
    embedded.put(key, list);
    apps.setApplications(embedded);
  }
  
  
  public static class TestApplication extends Application {
    
    
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
