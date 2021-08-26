/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */

package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenFetchingReceptionRequest {

  private static final String FORMAT = "TestFormat";

  private final ReceptionRequest reception = new ReceptionRequest();

  @BeforeEach
  public void init() {
    reception.setFormat("sip_zip");
  }

  @Test
  void fetchDefaultFormat() {
    assertEquals("sip_zip", reception.getFormat(), "Reception Default Format");
  }

  @Test
  void fetchName() {
    reception.setFormat(FORMAT);
    assertEquals(FORMAT, reception.getFormat(), "Reception Format");
  }

}
