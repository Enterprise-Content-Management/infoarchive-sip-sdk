/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.emc.ia.sdk.support.test.TestCase;


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

    byte[] actual = Unzip.file(zip).andProcessEntry(entry, stream -> readContents(stream));

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
