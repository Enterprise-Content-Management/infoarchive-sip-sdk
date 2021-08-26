/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class WhenFetchingReceptionResponse {

  private final ReceptionResponse receptionResponse = new ReceptionResponse();

  @BeforeEach
  public void init() {
    receptionResponse.setName("TestApplication");
  }

  @Test
  void fetchName() {
    assertEquals("TestApplication", receptionResponse.getName(), "Resource Name");
  }

}
