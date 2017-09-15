/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;


public class WhenFormattingAsJson {

  private final JsonFormatter formatter = new JsonFormatter();

  @Test
  public void formatString() throws IOException {
    assertEquals("Resource Name", "\"ape\"", formatter.format("ape"));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerException() throws IOException {
    formatter.format(null);
  }

}
