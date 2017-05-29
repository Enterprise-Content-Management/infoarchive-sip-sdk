/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */

package com.emc.ia.sdk.sip.client.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WhenFetchingReceptionRequest {

  private static final String FORMAT = "TestFormat";

  private final ReceptionRequest reception = new ReceptionRequest();

  @Test
  public void fetchDefaultFormat() {
    assertEquals("Reception Default Format", reception.getFormat(), "sip_zip");
  }

  @Test
  public void fetchName() {
    reception.setFormat(FORMAT);
    assertEquals("Reception Format", reception.getFormat(), FORMAT);
  }

  @Test
  public void validateIngestionResponseToStringValue() {
    assertEquals("Response ToString check", reception.toString(), "format=sip_zip");
  }

}
