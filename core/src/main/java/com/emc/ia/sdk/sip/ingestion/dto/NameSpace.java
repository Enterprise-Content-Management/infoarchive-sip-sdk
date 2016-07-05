/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class NameSpace {
  private String uri;
  private String prefix;

  public NameSpace() {
  }

  public NameSpace(String uri, String prefix) {
    this.uri = uri;
    this.prefix = prefix;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
