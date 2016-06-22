/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WhenFormattingStringUisngJSONFormatter {

  private final JSONFormatter formatter =  new JSONFormatter();
  private final static String STR = "TestString";
  
  @Test
  public void formatString() {
    String result = formatter.format(STR);   
    assertEquals("Resource Name", result, "\"TestString\"");
  }
  
  @Test (expected = RuntimeException.class)
  public void shouldThrowException() {   
    formatter.format(null);   
  }
  
}
