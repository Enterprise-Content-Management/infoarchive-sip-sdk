/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.emc.ia.sdk.support.rest.JsonFormatter;


public class WhenFormattingAsJson {

  private static final String STR = "TestString";

  private final JsonFormatter formatter = new JsonFormatter();

  @Test
  public void formatString() throws IOException {
    String result = formatter.format(STR);
    assertEquals("Resource Name", result, "\"TestString\"");
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerException() throws IOException {
    formatter.format(null);
  }
}
