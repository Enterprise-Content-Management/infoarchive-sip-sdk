/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.rest;


/**
 * A link in a hypermedia document.
 */
public class Link {

  private String href;

  public Link() {
    this(null);
  }

  public Link(String href) {
    setHref(href);
  }

  public String getHref() {
    return href;
  }

  public final void setHref(String href) {
    this.href = href;
  }

  @Override
  public String toString() {
    return href;
  }

}
