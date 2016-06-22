/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * Delete a file.
 */
public final class Delete {

  private Delete() {
    // Utility class
  }

  /**
   * Delete a given file. Ignores non-existing files.
   * @param file The file to delete
   */
  public static void file(File file) {
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

}
