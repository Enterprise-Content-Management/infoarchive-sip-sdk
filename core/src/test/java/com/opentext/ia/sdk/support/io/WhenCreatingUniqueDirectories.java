/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WhenCreatingUniqueDirectories {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void shouldReturnExistingDirectory() throws IOException {
    File dir = UniqueDirectory.in(temporaryFolder.newFolder());

    assertTrue("Directory doesn't exist", dir.exists());
  }

  @Test
  public void shouldThrowExceptionWhenUnableToCreateDirectory() throws IOException {
    assertThrows(RuntimeException.class, () -> UniqueDirectory.in(temporaryFolder.newFile()));
  }

}
