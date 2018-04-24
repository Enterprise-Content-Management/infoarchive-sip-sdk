/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.junit.rules.TemporaryFolder;


public class TestCase {

  private final RandomData random = new RandomData();

  protected File someFile(TemporaryFolder temporaryFolder) throws IOException {
    return file(temporaryFolder, randomBytes());
  }

  protected File file(TemporaryFolder temporaryFolder, byte[] contents) throws IOException {
    File result = temporaryFolder.newFile();
    try (OutputStream stream = Files.newOutputStream(result.toPath(), StandardOpenOption.WRITE)) {
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

  protected String randomUri() {
    return String.format("http://%s.com/%s", randomString(5), randomString(8));
  }

}
