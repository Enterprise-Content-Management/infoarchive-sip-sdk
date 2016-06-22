/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenExtractingDigitalObjects extends TestCase {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
  private final byte[] content = randomString().getBytes(StandardCharsets.UTF_16);
  private final String referenceInformation = randomString();

  @Test
  public void shouldExtractFromFile() throws IOException {
    assertDigitalObject(DigitalObject.fromFile(referenceInformation, file(folder, content)));
  }

  private void assertDigitalObject(DigitalObject actual) throws IOException {
    assertEquals("Reference information", referenceInformation, actual.getReferenceInformation());
    try (InputStream stream = actual.get()) {
      assertArrayEquals("Content", content, IOUtils.toByteArray(stream));
    }
  }

  @Test
  public void shouldExtractFromPath() throws IOException {
    assertDigitalObject(DigitalObject.fromPath(referenceInformation, file(folder, content).toPath()));
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
