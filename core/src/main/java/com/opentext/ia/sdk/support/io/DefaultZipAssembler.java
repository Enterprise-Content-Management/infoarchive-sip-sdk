/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Default implementation of {@linkplain ZipAssembler}.
 */
public class DefaultZipAssembler implements ZipAssembler {

  private static final int BUFFER_SIZE = 64 * 1024;

  private ZipOutputStream zip;

  @Override
  public void begin(OutputStream stream) {
    zip = new ZipOutputStream(new BufferedOutputStream(stream));
  }

  @Override
  public Collection<EncodedHash> addEntry(String name, InputStream stream, HashAssembler hashAssembler)
      throws IOException {
    hashAssembler.initialize();
    zip.putNextEntry(new ZipEntry(name));
    try {
      try (InputStream input = new BufferedInputStream(stream)) {
        IOStreams.copy(input, zip, BUFFER_SIZE, hashAssembler);
      }
    } finally {
      zip.closeEntry();
    }
    return hashAssembler.get();
  }

  /**
   * Close the ZIP to make it available for use.
   */
  @Override
  public void close() throws IOException {
    zip.close();
  }

}
