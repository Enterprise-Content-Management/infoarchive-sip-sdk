/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class WhenFetchingIngestionResponse {

  //private final TestIngestionResponse ingestionResponse = new TestIngestionResponse();
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

  @Test
  public void validateIngestionResponseToStringValue() {
    assertTrue(ingestionResponse.toString().contains("aipId=TestID; name=TestApplication; links=com.emc.ia.sdk.sip.ingestion.dto.IngestionResponse@"));
  }
}
