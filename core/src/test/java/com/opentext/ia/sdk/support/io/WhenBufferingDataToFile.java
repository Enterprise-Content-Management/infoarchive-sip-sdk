/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.TestCase;


class WhenBufferingDataToFile extends TestCase {

  @TempDir
  Path folder;

  @Test
  void shouldReadFileContents() throws IOException {
    byte[] expected = randomBytes();
    File file = newFile(folder, expected);
    FileBuffer buffer = new FileBuffer(file);

    byte[] actual = contentOf(buffer.openForReading());

    assertArrayEquals(expected, actual, "Read bytes");
  }

  private byte[] contentOf(InputStream stream) throws IOException {
    try (InputStream streamToClose = stream) {
      return IOUtils.toByteArray(streamToClose);
    }
  }

  @Test
  void shouldWriteFileContents() throws IOException {
    byte[] expected = randomBytes();
    File file = newFile(folder, expected);

    try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
      assertArrayEquals(expected, contentOf(stream), "Written bytes");
    }
  }

  @Test
  void shouldReportLength() throws IOException {
    File file = someFile(folder);
    FileBuffer buffer = new FileBuffer(file);

    long length = buffer.length();

    assertEquals(file.length(), length, "Length");
  }

}
