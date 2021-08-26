/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.opentext.ia.yaml.core.Value;

public class TestCase {

  private final RandomData random = new RandomData();

  protected final File someFile(Path temporaryFolder) throws IOException {
    return newFile(temporaryFolder, randomBytes());
  }

  protected final File newFolder(Path temporaryFolder) throws IOException {
    return newFolder(temporaryFolder, randomString(16));
  }

  protected final File newFolder(Path temporaryFolder, String name) throws IOException {
    return Files.createDirectory(temporaryFolder.resolve(name)).toFile();
  }

  protected final File newFile(Path temporaryFolder) throws IOException {
    return Files.createFile(temporaryFolder.resolve(randomString(16))).toFile();
  }

  protected final File newFile(Path temporaryFolder, byte[] contents) throws IOException {
    File result = newFile(temporaryFolder);
    FileUtils.writeByteArrayToFile(result, contents);
    return result;
  }

  protected final File newFile(Path temporaryFolder, String contents) throws IOException {
    File result = newFile(temporaryFolder);
    FileUtils.writeStringToFile(result, contents, StandardCharsets.UTF_8);
    return result;
  }

  protected final byte[] randomBytes() {
    return random.bytes();
  }

  protected final String randomString() {
    return randomString(256);
  }

  protected final String randomString(int maxLength) {
    return random.string(maxLength);
  }

  protected final int randomInt(int max) {
    return random.integer(max);
  }

  protected final int randomInt(int min, int max) {
    return random.integer(min, max);
  }

  protected final String randomUri() {
    return String.format("http://%s.com/%s", randomString(5), randomString(8));
  }

  protected final void assertValue(String expected, Value actual, String message) {
    assertEquals(expected, actual.toString(), message);
  }
}
