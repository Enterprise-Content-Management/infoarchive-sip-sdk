/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

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

  private static final int BUFFER_SIZE = 65536;

  private ZipOutputStream zip;

  @Override
  public void begin(OutputStream stream) {
    zip = new ZipOutputStream(stream);
  }

  @Override
  public Collection<EncodedHash> addEntry(String name, InputStream stream, HashAssembler hashAssembler)
      throws IOException {
    hashAssembler.initialize();
    zip.putNextEntry(new ZipEntry(name));
    try {
      IOStreams.copy(stream, zip, BUFFER_SIZE, hashAssembler);
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
