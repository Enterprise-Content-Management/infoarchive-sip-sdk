/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * Implementation of {@linkplain InputStream} from a string.
 */
public class StringStream extends ByteArrayInputStream {

  public StringStream(String text) {
    this(text, StandardCharsets.UTF_8);
  }

  public StringStream(String text, Charset charset) {
    super(text.getBytes(charset));
  }

}
