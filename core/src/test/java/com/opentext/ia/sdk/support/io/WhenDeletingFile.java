/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.RandomData;
import com.opentext.ia.test.TestCase;


class WhenDeletingFile extends TestCase {

  @TempDir
  Path temporaryFolder;
  private final RandomData random = new RandomData();

  @Test
  void shouldRemoveFile() throws IOException {
    File file = newFile(temporaryFolder);

    Delete.file(file);

    assertFalse(file.exists(), "File still exists");
  }

  @Test
  void shouldNotComplainAboutNonExistingFiles() {
    Delete.file(new File(random.string()));
  }

}
