/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.rules.TemporaryFolder;


public class TestCase {

  private final RandomData random = new RandomData();

  protected File someFile(TemporaryFolder temporaryFolder) throws IOException {
    return file(temporaryFolder, randomBytes());
  }

  protected File file(TemporaryFolder temporaryFolder, byte[] contents) throws IOException {
    File result = temporaryFolder.newFile();
    try (OutputStream stream = new FileOutputStream(result)) {
      stream.write(contents);
    }
    return result;
  }

  protected byte[] randomBytes() {
    return random.bytes();
  }

  protected String randomString() {
    return randomString(256);
  }

  protected String randomString(int maxLength) {
    return random.string(maxLength);
  }

  protected int randomInt(int max) {
    return random.integer(max);
  }

  protected int randomInt(int min, int max) {
    return random.integer(min, max);
  }

}
