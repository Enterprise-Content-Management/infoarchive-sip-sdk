/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.emc.ia.sdk.support.test.TestCase;


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
