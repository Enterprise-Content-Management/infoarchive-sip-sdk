/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

public class TextPart extends Part {

  private final String text;

  public TextPart(String name, String text) {
    this(name, MediaTypes.TEXT, text);
  }

  public TextPart(String name, String mediaType, String text) {
    super(name, mediaType);
    this.text = text;
  }

  public String getText() {
    return text;
  }

}
