/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class IngestConfig extends NamedLinkContainer {

  private String sipFormat;
  private String ingest;

  public IngestConfig() {
    setSipFormat("sip_zip");
  }

  public String getSipFormat() {
    return sipFormat;
  }

  public final void setSipFormat(String sipFormat) {
    this.sipFormat = sipFormat;
  }

  public String getIngest() {
    return ingest;
  }

  public void setIngest(String ingest) {
    this.ingest = ingest;
  }

}
