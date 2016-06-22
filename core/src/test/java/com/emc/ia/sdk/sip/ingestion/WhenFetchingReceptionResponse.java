/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class WhenFetchingReceptionResponse {
 
  private final ReceptionResponse app = new ReceptionResponse();
  
  @Before
  public void init() {
    app.setName("TestApplication");
    app.setAipId("TestID");
  }
  
  @Test
  public void fetchName() {    
    assertEquals("Resource Name", app.getName(), "TestApplication");
  }
  
  @Test
  public void fetchAipID() {    
    assertEquals("AipId", app.getAipId(), "TestID");
  }
  
  @Test
  public void validateLinkToStringValue() {        
    assertEquals("Resource ToString check", app.toString(), "ReceptionResponse [name=" + "TestApplication" + ", aipId=" + "TestID" + "]");
  } 
  
}
