/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import com.opentext.ia.sdk.support.JavaBean;


public class Namespace extends JavaBean {

  private String uri;
  private String prefix;

  public Namespace() {
  }

  public Namespace(String prefix, String uri) {
    super();
    this.prefix = prefix;
    this.uri = uri;
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
