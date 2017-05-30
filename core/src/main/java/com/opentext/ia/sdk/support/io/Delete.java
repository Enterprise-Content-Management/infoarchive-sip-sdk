/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.File;

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
    if (file.isFile()) {
      deleteFile(file);
    }
  }

  private static void deleteFile(File file) {
    if (!file.delete()) {
      file.deleteOnExit();
    }
  }

}
