/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class ReceptionRequest {

  private String format;

  public ReceptionRequest() {
    setFormat("sip_zip");
  }

  public String getFormat() {
    return format;
  }

  public final void setFormat(String format) {
    this.format = format;
  }

  @Override
  public String toString() {
    return String.format("format=%s", format);
  }

}
