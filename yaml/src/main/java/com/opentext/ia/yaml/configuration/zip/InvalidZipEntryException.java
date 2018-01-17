/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.File;


public class InvalidZipEntryException extends RuntimeException {

  private final File file;

  public InvalidZipEntryException(File file, Throwable cause) {
    super("Invalid file: " + file, cause);
    this.file = file;
  }

  public File getFile() {
    return file;
  }

}
