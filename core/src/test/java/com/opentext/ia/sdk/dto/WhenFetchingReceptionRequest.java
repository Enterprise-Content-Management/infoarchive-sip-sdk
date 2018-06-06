/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */

package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class WhenFetchingReceptionRequest {

  private static final String FORMAT = "TestFormat";

  private final ReceptionRequest reception = new ReceptionRequest();

  @Test
  public void fetchDefaultFormat() {
    assertEquals("Reception Default Format", "sip_zip", reception.getFormat());
  }

  @Test
  public void fetchName() {
    reception.setFormat(FORMAT);
    assertEquals("Reception Format", FORMAT, reception.getFormat());
  }

}
