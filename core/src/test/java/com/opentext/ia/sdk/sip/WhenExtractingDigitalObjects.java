/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.TestCase;


public class WhenExtractingDigitalObjects extends TestCase {

  @TempDir
  public Path folder;
  private final byte[] content = randomString().getBytes(StandardCharsets.UTF_16);
  private final String referenceInformation = randomString();

  @Test
  public void shouldExtractFromFile() throws IOException {
    assertDigitalObject(DigitalObject.fromFile(referenceInformation, newFile(folder, content)));
  }

  private void assertDigitalObject(DigitalObject actual) throws IOException {
    assertEquals(referenceInformation, actual.getReferenceInformation(), "Reference information");
    try (InputStream stream = actual.get()) {
      assertArrayEquals(content, IOUtils.toByteArray(stream), "Content");
    }
    assertEquals(content.length, actual.getSize(), "Size");
  }

  @Test
  public void shouldExtractFromPath() throws IOException {
    assertDigitalObject(DigitalObject.fromPath(referenceInformation, newFile(folder, content).toPath()));
  }

  @Test
  public void shouldExtractFromBytes() throws IOException {
    assertDigitalObject(DigitalObject.fromBytes(referenceInformation, content));
  }

  @Test
  public void shouldExtractFromString() throws IOException {
    assertDigitalObject(DigitalObject.fromString(referenceInformation, new String(content, StandardCharsets.UTF_16),
        StandardCharsets.UTF_16));
  }

}
