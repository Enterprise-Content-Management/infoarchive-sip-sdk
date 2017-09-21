/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * In-memory buffer of data.
 */
public class MemoryBuffer implements DataBuffer {

  private final ByteArrayInputOutputStream buffer = new ByteArrayInputOutputStream();

  @Override
  public OutputStream openForWriting() throws IOException {
    return buffer;
  }

  @Override
  public InputStream openForReading() throws IOException {
    return buffer.getInputStream();
  }

  @Override
  public long length() {
    return buffer.size();
  }

}
