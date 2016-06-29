/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class WhenCreatingUniqueDirectories {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldReturnExistingDirectory() throws IOException {
    File dir = UniqueDirectory.in(temporaryFolder.newFolder());

    assertTrue("Directory doesn't exist", dir.exists());
  }

  @Test
  public void shouldThrowExceptionWhenUnableToCreateDirectory() throws IOException {
    thrown.expect(RuntimeIoException.class);
    UniqueDirectory.in(temporaryFolder.newFile());
  }


}
