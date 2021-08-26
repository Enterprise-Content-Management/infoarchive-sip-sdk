/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenFetchingJobDefinition {

  private static final String HANDLERNAME = "TestHandler";

  private final JobDefinition instance = new JobDefinition();

  @BeforeEach
  public void init() throws IOException {
    instance.setHandlerName("ConfirmationJob");
  }

  @Test
  void fetchDefaultHandlerName() {
    assertEquals("ConfirmationJob", instance.getHandlerName(),
        "Job Definition Default Handler Name");
  }

  @Test
  void fetchHandlerName() {
    instance.setHandlerName(HANDLERNAME);
    assertEquals(HANDLERNAME, instance.getHandlerName(), "Reception Format");
  }

}
