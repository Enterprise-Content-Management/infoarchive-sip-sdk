/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.opentext.ia.test.RandomData;


public class WhenReadingRepeatableInputStreamMultipleTimes {

  private final RandomData random = new RandomData();

  @Test
  public void shouldReturnSameContentsEveryTime() throws IOException {
    byte[] contents = random.bytes();
    Supplier<InputStream> supplier = new RepeatableInputStream(new ByteArrayInputStream(contents));

    assertArrayEquals(contents, getContents(supplier), "Contents #1");
    assertArrayEquals(contents, getContents(supplier), "Contents #2");
  }

  private byte[] getContents(Supplier<InputStream> supplier) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    IOUtils.copy(supplier.get(), stream);
    return stream.toByteArray();
  }

}
