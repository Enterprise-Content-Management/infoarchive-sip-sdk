/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WhenFetchingReception {
   
  private final Reception app = new Reception();
  private final static String FORMAT = "TestFormat";
  
  @Test
  public void fetchDefaultFormat() {
    assertEquals("Reception Default Format", app.getFormat(), "sip_zip");
  }
  
  @Test
  public void fetchName() {
    app.setFormat(FORMAT);
    assertEquals("Reception Format", app.getFormat(), FORMAT);
  }
    
  @Test
  public void validateReceptionToStringValue() {        
    app.setFormat(FORMAT);
    assertEquals("Reception ToString check", app.toString(), "Reception [format=" + FORMAT + "]");
  }
  
}
