/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.File;


public class InvalidZipEntryException extends RuntimeException {

  private final File file;

  public InvalidZipEntryException(Object source, Throwable cause) {
    super("Invalid file: " + source, cause);
    this.file = source instanceof File ? (File)source : null;
  }

  public File getFile() {
    return file;
  }

}
