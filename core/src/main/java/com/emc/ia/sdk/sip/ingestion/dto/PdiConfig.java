/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class PdiConfig {

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
