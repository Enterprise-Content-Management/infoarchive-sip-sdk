/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    String schemaName = "testSchema";
    schema.add(schemaName);
    helper.setResultSchema(schema);
    assertNotNull(helper.getResultSchema());
    assertEquals("Schema", schemaName, helper.getResultSchema().get(0));
  }

  @Test
  public void initResultConfigurationHelpers() {
    assertNotNull(new ResultConfigurationHelpers());
  }

}
