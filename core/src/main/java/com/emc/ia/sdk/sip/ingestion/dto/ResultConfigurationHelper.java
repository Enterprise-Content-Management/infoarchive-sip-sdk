/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.ArrayList;
import java.util.List;

public class ResultConfigurationHelper extends NamedLinkContainer {
  private List<String> resultSchema;

  public ResultConfigurationHelper() {
    setResultSchema(new ArrayList<String>());
  }
  public List<String> getResultSchema() {
    return resultSchema;
  }
  public void setResultSchema(List<String> resultSchema) {
    this.resultSchema = resultSchema;
  }
}
