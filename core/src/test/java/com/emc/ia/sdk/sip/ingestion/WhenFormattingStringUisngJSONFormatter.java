/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class WhenFormattingStringUisngJSONFormatter {

  private static final String STR = "TestString";

  private final JSONFormatter formatter = new JSONFormatter();

  @Test
  public void formatString() {
    String result = formatter.format(STR);
    assertEquals("Resource Name", result, "\"TestString\"");
  }

  @Test(expected = RuntimeException.class)
  public void shouldThrowException() {
    formatter.format(null);
  }

}
