/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class WhenFetchingReceptionResponse {

  private final ReceptionResponse receptionResponse = new ReceptionResponse();

  @Before
  public void init() {
    receptionResponse.setName("TestApplication");
  }

  @Test
  public void fetchName() {
    assertEquals("Resource Name", receptionResponse.getName(), "TestApplication");
  }

}
