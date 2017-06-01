/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.dto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class WhenWorkingWithResultConfigurationHelper {

  private final ResultConfigurationHelper helper = new ResultConfigurationHelper();

  @Test
  public void defaultResultSchemaShouldNotBeNULL() {
    assertNotNull(helper.getResultSchema());
  }

  @Test
  public void setResultSchema() {
    List<String> schema = new ArrayList<>();
    schema.add("testSchema");
    helper.setResultSchema(schema);
    assertNotNull(helper.getResultSchema());
    assertEquals(helper.getResultSchema()
      .get(0), "testSchema");
  }

  @Test
  public void initResultConfigurationHelpers() {
    assertNotNull(new ResultConfigurationHelpers());
  }

}
