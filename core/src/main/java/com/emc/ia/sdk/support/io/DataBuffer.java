/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Something that can hold arbitrary data. The data can be both {@linkplain #openForWriting() written} and
 * {@linkplain #openForReading() read} back.
 */
public interface DataBuffer {

  /**
   * Read data from the buffer using an {@linkplain InputStream}.
   * @return The stream for reading data
   * @throws IOException When an I/O error occurs
   */
  InputStream openForReading() throws IOException;

  /**
   * Write data into the buffer using an {@linkplain OutputStream}.
   * @return The stream for writing data
   * @throws IOException When an I/O error occurs
   */
  OutputStream openForWriting() throws IOException;

  /**
   * Return the number of bytes in the buffer.
   * @return The number of bytes in the buffer
   */
  long length();

}
