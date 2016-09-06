/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

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
