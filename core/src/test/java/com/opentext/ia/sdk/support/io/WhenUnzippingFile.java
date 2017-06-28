/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.assertArrayEquals;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.test.TestCase;


public class WhenUnzippingFile extends TestCase {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void shouldProcessEntry() throws IOException {
    String entry = randomString();
    byte[] expected = randomBytes();
    File zip = temporaryFolder.newFile();
    try (OutputStream out = new FileOutputStream(zip)) {
      ZipAssembler zipper = new DefaultZipAssembler();
      zipper.begin(out);
      zipper.addEntry(entry, new ByteArrayInputStream(expected), new NoHashAssembler());
      zipper.close();
    }

    byte[] actual = Unzip.file(zip)
      .andProcessEntry(entry, stream -> readContents(stream));

    assertArrayEquals("Contents", expected, actual);
  }

  private byte[] readContents(InputStream stream) {
    try {
      return IOUtils.toByteArray(stream);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

}
