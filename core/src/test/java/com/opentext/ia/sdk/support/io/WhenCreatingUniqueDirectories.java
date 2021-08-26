/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.TestCase;

class WhenCreatingUniqueDirectories extends TestCase {

  @TempDir
  Path temporaryFolder;

  @Test
  void shouldReturnExistingDirectory() throws IOException {
    File dir = UniqueDirectory.in(newFolder(temporaryFolder));

    assertTrue(dir.exists(), "Directory doesn't exist");
  }

  @Test
  void shouldThrowExceptionWhenUnableToCreateDirectory() throws IOException {
    assertThrows(RuntimeException.class, () -> UniqueDirectory.in(newFile(temporaryFolder)));
  }

}
