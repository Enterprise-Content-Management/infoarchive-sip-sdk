/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhenFetchingJobInstance {

  private static final String STATUS = "SCHEDULED";

  private final JobInstance instance = new JobInstance();

  @BeforeEach
  public void init() throws IOException {
    instance.setStatus("SUCCESS");
  }

  @Test
  void fetchDefaultStatus() {
    assertEquals("SUCCESS", instance.getStatus(), "Job Instance Default Format");
  }

  @Test
  void fetchStatus() {
    instance.setStatus(STATUS);
    assertEquals(STATUS, instance.getStatus(), "Reception Format");
  }

  @Test
  void invokeJobInstances() {
    JobInstances defs = new JobInstances();
    assertNotNull(defs);
  }

}
