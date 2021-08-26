/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;


public class WhenFormattingAsJson {

  private final JsonFormatter formatter = new JsonFormatter();

  @Test
  public void formatString() throws IOException {
    assertEquals("\"ape\"", formatter.format("ape"), "Resource Name");
  }

  @Test
  public void shouldThrowNullPointerException() throws IOException {
    assertThrows(NullPointerException.class, () -> formatter.format(null));
  }

}
