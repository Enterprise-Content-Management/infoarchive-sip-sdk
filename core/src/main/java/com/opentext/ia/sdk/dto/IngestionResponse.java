/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class IngestionResponse extends NamedLinkContainer {

  private String aipId;

  public String getAipId() {
    return aipId;
  }

  public void setAipId(String aipId) {
    this.aipId = aipId;
  }

  @Override
  public String toString() {
    return String.format("aipId=%s; %s", aipId, super.toString());
  }

}
