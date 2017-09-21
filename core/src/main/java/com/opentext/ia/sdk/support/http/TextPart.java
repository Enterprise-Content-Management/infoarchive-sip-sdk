/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;


/**
 * A text-based part in a <a href="https://tools.ietf.org/html/rfc2045">multi-part</a> message.
 */

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
