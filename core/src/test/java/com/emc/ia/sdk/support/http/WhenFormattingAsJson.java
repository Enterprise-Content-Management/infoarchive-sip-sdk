/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

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
