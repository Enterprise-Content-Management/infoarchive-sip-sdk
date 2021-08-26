/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;


class WhenWorkingWithResultConfigurationHelper {

  private final ResultConfigurationHelper helper = new ResultConfigurationHelper();

  @Test
  void defaultResultSchemaShouldNotBeNULL() {
    assertNotNull(helper.getResultSchema());
  }

  @Test
  void setResultSchema() {
    List<String> schema = new ArrayList<>();
    String schemaName = "testSchema";
    schema.add(schemaName);
    helper.setResultSchema(schema);
    assertNotNull(helper.getResultSchema());
    assertEquals(schemaName, helper.getResultSchema().get(0), "Schema");
  }

  @Test
  void initResultConfigurationHelpers() {
    assertNotNull(new ResultConfigurationHelpers());
  }

}
