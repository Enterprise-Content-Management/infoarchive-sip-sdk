/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

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
