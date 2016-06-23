/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class WhenFetchingReceptionResponse {

  private final ReceptionResponse receptionResponse = new ReceptionResponse();

  @Before
  public void init() {
    receptionResponse.setName("TestApplication");
    receptionResponse.setAipId("TestID");
  }

  @Test
  public void fetchName() {
    assertEquals("Resource Name", receptionResponse.getName(), "TestApplication");
  }

  @Test
  public void fetchAipID() {
    assertEquals("AipId", receptionResponse.getAipId(), "TestID");
  }

}
