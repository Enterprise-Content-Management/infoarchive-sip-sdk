/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.test.RandomData;


public class WhenCopyingFiles {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private final RandomData random = new RandomData();

  @Test
  public void shouldCreateFileWithSameNameAndContentsInProvidedDirectory() throws IOException {
    File source = temporaryFolder.newFile();
    String contents = random.string();
    setContents(source, contents);
    File destinationDir = temporaryFolder.newFolder();

    CopyFile.from(source)
      .to(destinationDir);

    File destination = new File(destinationDir, source.getName());
    assertTrue("File not copied", destination.exists());
    assertEquals("File contents", contents, getContents(destination));
  }

  private void setContents(File source, String contents) throws IOException {
    try (PrintWriter writer = new PrintWriter(source, StandardCharsets.UTF_8.toString())) {
      writer.print(contents);
    }
  }

  private String getContents(File destination) throws IOException {
    try (InputStream stream = Files.newInputStream(destination.toPath(), StandardOpenOption.READ)) {
      return IOUtils.toString(stream, StandardCharsets.UTF_8);
    }
  }

}
