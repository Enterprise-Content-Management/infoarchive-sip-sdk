/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class WhenFetchingIngestionResponse {

  // private final TestIngestionResponse ingestionResponse = new TestIngestionResponse();
  private final IngestionResponse ingestionResponse = new IngestionResponse();

  @Before
  public void init() {
    ingestionResponse.setName("TestApplication");
    ingestionResponse.setAipId("TestID");
  }

  @Test
  public void fetchName() {
    assertEquals("Resource Name", ingestionResponse.getName(), "TestApplication");
  }

  @Test
  public void fetchAipID() {
    assertEquals("AipId", ingestionResponse.getAipId(), "TestID");
  }

}
