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
    setUri(uri);
    setPrefix(prefix);
  }

  public String getUri() {
    return uri;
  }

  public final void setUri(String uri) {
    this.uri = uri;
  }

  public String getPrefix() {
    return prefix;
  }

  public final void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
