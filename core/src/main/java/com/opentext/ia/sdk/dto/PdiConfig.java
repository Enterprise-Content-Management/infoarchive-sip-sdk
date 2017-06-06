/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import com.opentext.ia.sdk.support.JavaBean;


public class PdiConfig extends JavaBean {

  private String schema;
  private String pdi;

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getPdi() {
    return pdi;
  }

  public void setPdi(String pdi) {
    this.pdi = pdi;
  }

}
