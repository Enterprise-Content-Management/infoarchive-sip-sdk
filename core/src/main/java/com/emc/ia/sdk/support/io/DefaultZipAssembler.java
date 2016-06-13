/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
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
    copyStream(stream, hashAssembler);
    zip.closeEntry();
    return hashAssembler.get();
  }

  private void copyStream(InputStream stream, HashAssembler hashAssembler) throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int numRead = stream.read(buffer);
    while (numRead > 0) {
      zip.write(buffer, 0, numRead);
      hashAssembler.add(buffer, numRead);

      numRead = stream.read(buffer);
    }
  }

  /**
   * Close the ZIP to make it available for use.
   */
  @Override
  public void close() throws IOException {
    zip.close();
  }

}
