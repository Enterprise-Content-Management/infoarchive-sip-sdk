/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.opentext.ia.test.TestCase;


class WhenZipping extends TestCase {

  private final ZipAssembler zip = new DefaultZipAssembler();
  private final ByteArrayInputOutputStream output = new ByteArrayInputOutputStream();

  @Test
  void shouldAddEntries() throws IOException {
    String name1 = someName();
    byte[] content1 = randomBytes();
    String name2 = someName();
    byte[] content2 = randomBytes();
    HashAssembler hashAssembler = new NoHashAssembler();

    zip.begin(output);
    zip.addEntry(name1, new ByteArrayInputStream(content1), hashAssembler);
    zip.addEntry(name2, new ByteArrayInputStream(content2), hashAssembler);
    zip.close();

    try (ZipInputStream unzip = new ZipInputStream(output.getInputStream())) {
      ZipEntry entry = unzip.getNextEntry();
      assertNotNull(entry, "Missing entry #1");
      assertEquals(name1, entry.getName(), "Zip entry #1");
      assertArrayEquals(content1, IOUtils.toByteArray(unzip), "Zip content #1");

      unzip.closeEntry();
      entry = unzip.getNextEntry();
      assertNotNull(entry, "Missing entry #2");
      assertEquals(name2, entry.getName(), "Zip entry #2");
      assertArrayEquals(content2, IOUtils.toByteArray(unzip), "Zip content #2");

      unzip.closeEntry();
      entry = unzip.getNextEntry();
      assertNull(entry, "Additional zip entries");
    }
  }

  private String someName() {
    return randomString(32);
  }

  @Test
  void shouldCalculateHashes() throws IOException {
    HashAssembler hashAssembler = mock(HashAssembler.class);
    Collection<EncodedHash> expected = Collections.singletonList(someHash());
    when(hashAssembler.get()).thenReturn(expected);
    byte[] content = randomBytes();
    zip.begin(output);
    Collection<EncodedHash> actual = zip.addEntry(someName(), new ByteArrayInputStream(content), hashAssembler);
    zip.close();

    verify(hashAssembler).initialize();
    ArgumentCaptor<byte[]> argument = ArgumentCaptor.forClass(byte[].class);
    verify(hashAssembler).add(argument.capture(), eq(content.length));
    assertArrayEquals(content, Arrays.copyOf(argument.getValue(), content.length), "Content");
    assertEquals(expected, actual, "Hash");
  }

  private EncodedHash someHash() {
    return new EncodedHash(someName(), someName(), someName());
  }

}
