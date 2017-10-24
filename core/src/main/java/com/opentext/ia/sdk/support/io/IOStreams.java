/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;


/**
 * Utility methods to work with I/O streams.
 */
public final class IOStreams {

  private IOStreams() {
    // Utility class
  }

  /**
   * Utility method to copy the bytes from an InputStream to an OutputStream while also assembling a hash value in the
   * process.
   * @param in The source stream
   * @param out The target stream
   * @param bufferSize The size of the internal buffer
   * @param hashAssembler The HashAssembler to use.
   * @throws IOException if an error occurs when copying the streams
   */
  public static void copy(InputStream in, OutputStream out, int bufferSize, HashAssembler hashAssembler)
      throws IOException {
    byte[] buffer = new byte[bufferSize];
    int numRead = Objects.requireNonNull(in, "Missing input").read(buffer);
    if (numRead == 0) {
      throw new IllegalArgumentException("Missing content");
    }
    Objects.requireNonNull(out, "Missing output");
    while (numRead > 0) {
      out.write(buffer, 0, numRead);
      hashAssembler.add(buffer, numRead);
      numRead = in.read(buffer);
    }
  }

  public static void close(Closeable... closeables) {
    for (Closeable closeable : closeables) {
      if (closeable == null) {
        continue;
      }
      try {
        closeable.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }

}
