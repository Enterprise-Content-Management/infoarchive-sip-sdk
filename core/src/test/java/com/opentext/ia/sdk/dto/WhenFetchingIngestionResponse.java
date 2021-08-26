/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class WhenFetchingIngestionResponse {

  // private final TestIngestionResponse ingestionResponse = new TestIngestionResponse();
  private final IngestionResponse ingestionResponse = new IngestionResponse();

  @BeforeEach
  public void init() {
    ingestionResponse.setName("TestApplication");
    ingestionResponse.setAipId("TestID");
  }

  @Test
  void fetchName() {
    assertEquals("TestApplication", ingestionResponse.getName(), "Resource Name");
  }

  @Test
  void fetchAipID() {
    assertEquals("TestID", ingestionResponse.getAipId(), "AipId");
  }

}
