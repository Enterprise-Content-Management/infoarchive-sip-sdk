/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;


public class ResultConfigurationHelper extends NamedLinkContainer {

  private List<String> resultSchema = new ArrayList<>();

  public List<String> getResultSchema() {
    return resultSchema;
  }

  public void setResultSchema(List<String> resultSchema) {
    this.resultSchema = resultSchema;
  }

}
