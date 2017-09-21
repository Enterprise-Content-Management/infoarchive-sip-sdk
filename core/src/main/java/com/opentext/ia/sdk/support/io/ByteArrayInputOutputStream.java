/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@linkplain OutputStream} that stores its data in memory and allows it to be {@linkplain #getInputStream() read}
 * back.
 */
public class ByteArrayInputOutputStream extends ByteArrayOutputStream {

  /**
   * @return The stored data as an {@linkplain InputStream}
   */
  public InputStream getInputStream() {
    return new ByteArrayInputStream(buf, 0, count);
  }

}
