/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

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
