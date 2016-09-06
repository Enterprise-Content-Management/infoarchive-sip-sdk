/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class PdiSchema extends NamedLinkContainer {

  private String format;

  public PdiSchema() {
    setFormat("xsd");
  }

  public String getFormat() {
    return format;
  }

  public final void setFormat(String format) {
    this.format = format;
  }

}
