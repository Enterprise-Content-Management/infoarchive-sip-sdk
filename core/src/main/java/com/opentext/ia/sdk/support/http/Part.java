/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;


/**
 * One part in a <a href="https://tools.ietf.org/html/rfc2045">multi-part</a> message.
 */
public class Part {

  private final String name;
  private final String mediaType;

  public Part(String name, String mediaType) {
    this.name = name;
    this.mediaType = mediaType;
  }

  public String getName() {
    return name;
  }

  public String getMediaType() {
    return mediaType;
  }

}
