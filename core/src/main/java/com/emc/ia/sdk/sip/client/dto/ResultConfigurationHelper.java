/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

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
