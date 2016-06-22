/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */

package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class WhenFetchingReception {

  private static final String FORMAT = "TestFormat";

  private final Reception reception = new Reception();

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
  public void validateReceptionToStringValue() {
    reception.setFormat(FORMAT);
    assertEquals("Reception ToString check", reception.toString(), "Reception [format=" + FORMAT + "]");
  }

}
