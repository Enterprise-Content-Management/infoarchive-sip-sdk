/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.emc.ia.sdk.support.rest.JsonFormatter;
import com.fasterxml.jackson.core.JsonProcessingException;


public class WhenFormattingAsJson {

  private static final String STR = "TestString";

  private final JsonFormatter formatter = new JsonFormatter();

  @Test
  public void formatString() throws JsonProcessingException {
    String result = formatter.format(STR);
    assertEquals("Resource Name", result, "\"TestString\"");
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerException() throws JsonProcessingException {
    formatter.format(null);
  }
}
