/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.*;

import java.io.*;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.test.TestCase;


public class WhenBufferingDataToFile extends TestCase {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private File file;
  private DataBuffer buffer;

  @Test
  public void shouldReadFileContents() throws IOException {
    byte[] expected = randomBytes();
    file = file(folder, expected);
    buffer = new FileBuffer(file);

    byte[] actual = contentOf(buffer.openForReading());

    assertArrayEquals("Read bytes", expected, actual);
  }

  private byte[] contentOf(InputStream stream) throws IOException {
    try {
      return IOUtils.toByteArray(stream);
    } finally {
      stream.close();
    }
  }

  @Test
  public void shouldWriteFileContents() throws IOException {
    byte[] expected = randomBytes();
    file = folder.newFile();
    buffer = new FileBuffer(file);

    try (OutputStream stream = buffer.openForWriting()) {
      stream.write(expected);
    }

    try (InputStream stream = new FileInputStream(file)) {
      assertArrayEquals("Written bytes", expected, contentOf(stream));
    }
  }

  @Test
  public void shouldReportLength() throws IOException {
    file = folder.newFile();
    try (OutputStream stream = new FileOutputStream(file)) {
      stream.write(randomBytes());
    }
    buffer = new FileBuffer(file);

    long length = buffer.length();

    assertEquals("Length", file.length(), length);
  }

}
