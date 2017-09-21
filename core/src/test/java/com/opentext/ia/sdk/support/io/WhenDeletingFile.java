/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.test.RandomData;


public class WhenDeletingFile {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private final RandomData random = new RandomData();

  @Test
  public void shouldRemoveFile() throws IOException {
    File file = temporaryFolder.newFile();

    Delete.file(file);

    assertFalse("File still exists", file.exists());
  }

  @Test
  public void shouldNotComplainAboutNonExistingFiles() {
    Delete.file(new File(random.string()));
  }

}
