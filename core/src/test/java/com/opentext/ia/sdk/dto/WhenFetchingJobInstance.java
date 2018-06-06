/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class WhenFetchingJobInstance {

  private static final String STATUS = "SCHEDULED";

  private final JobInstance instance = new JobInstance();

  @Test
  public void fetchDefaultStatus() {
    assertEquals("Job Instance Default Format", "SUCCESS", instance.getStatus());
  }

  @Test
  public void fetchStatus() {
    instance.setStatus(STATUS);
    assertEquals("Reception Format", STATUS, instance.getStatus());
  }

  @Test
  public void invokeJobInstances() {
    JobInstances defs = new JobInstances();
    assertNotNull(defs);
  }

}
