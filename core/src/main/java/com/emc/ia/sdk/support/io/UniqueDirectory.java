/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * Create uniquely named directories in a given parent directory.
 */
public final class UniqueDirectory {

  private UniqueDirectory() {
    // Utility class
  }

  /**
   * Create a new directory in the given parent directory.
   * @param parentDir The parent directory in which to create a new directory
   * @return The newly created directory
   */
  public static File in(File parentDir) {
    File result = new File(parentDir, UUID.randomUUID().toString());
    if (!result.mkdirs()) {
      throw new RuntimeIoException(new IOException("Could not create directory: " + result));
    }
    return result;
  }

}
