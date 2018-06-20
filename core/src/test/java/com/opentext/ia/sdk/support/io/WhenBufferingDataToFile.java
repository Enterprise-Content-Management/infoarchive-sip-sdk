/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.test.TestCase;


public class WhenBufferingDataToFile extends TestCase {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void shouldReadFileContents() throws IOException {
    byte[] expected = randomBytes();
    File file = file(folder, expected);
    FileBuffer buffer = new FileBuffer(file);

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
    File file = folder.newFile();
    FileBuffer buffer = new FileBuffer(file);

    try (OutputStream stream = buffer.openForWriting()) {
      stream.write(expected);
    }

    try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
      assertArrayEquals("Written bytes", expected, contentOf(stream));
    }
  }

  @Test
  public void shouldReportLength() throws IOException {
    File file = folder.newFile();
    try (OutputStream stream = Files.newOutputStream(file.toPath(), StandardOpenOption.WRITE)) {
      stream.write(randomBytes());
    }
    FileBuffer buffer = new FileBuffer(file);

    long length = buffer.length();

    assertEquals("Length", file.length(), length);
  }

}
