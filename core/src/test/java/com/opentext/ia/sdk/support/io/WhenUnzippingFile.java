/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.TestCase;

public class WhenUnzippingFile extends TestCase {

  @TempDir
  public Path temporaryFolder;

  @Test
  public void shouldProcessEntry() throws IOException {
    String entry = randomString();
    byte[] expected = randomBytes();
    File zip = newFile(temporaryFolder);
    try (OutputStream out = Files.newOutputStream(zip.toPath(), StandardOpenOption.CREATE);
        ZipAssembler zipper = new DefaultZipAssembler()) {
      zipper.begin(out);
      zipper.addEntry(entry, new ByteArrayInputStream(expected), new NoHashAssembler());
    }

    byte[] actual = Unzip.file(zip).andProcessEntry(entry, stream -> readContents(stream));

    assertArrayEquals(expected, actual, "Contents");
  }

  private byte[] readContents(InputStream stream) {
    try {
      return IOUtils.toByteArray(stream);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

}
