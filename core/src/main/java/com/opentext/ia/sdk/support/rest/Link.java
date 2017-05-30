/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.rest;

public class Link {

  private String href;

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  @Override
  public String toString() {
    return href;
  }

}
