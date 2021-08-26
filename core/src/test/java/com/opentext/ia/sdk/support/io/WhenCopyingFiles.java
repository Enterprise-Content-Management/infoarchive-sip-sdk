/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.RandomData;
import com.opentext.ia.test.TestCase;

class WhenCopyingFiles extends TestCase {

  @TempDir
  Path temporaryFolder;
  private final RandomData random = new RandomData();

  @Test
  void shouldCreateFileWithSameNameAndContentsInProvidedDirectory() throws IOException {
    String contents = random.string();
    File source = newFile(temporaryFolder, contents);
    File destinationDir = newFolder(temporaryFolder);

    CopyFile.from(source).to(destinationDir);

    File destination = new File(destinationDir, source.getName());
    assertTrue(destination.exists(), "File not copied");
    assertEquals(contents, getContents(destination), "File contents");
  }

  private String getContents(File destination) throws IOException {
    try (InputStream stream = Files.newInputStream(destination.toPath(), StandardOpenOption.READ)) {
      return IOUtils.toString(stream, StandardCharsets.UTF_8);
    }
  }

}
