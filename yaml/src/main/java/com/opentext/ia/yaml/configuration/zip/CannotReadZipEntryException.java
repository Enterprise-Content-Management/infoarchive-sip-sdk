/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.File;


/**
 * Exception that indicates a file could not be added to a ZIP because it can't be read or has syntax errors.
 * @author Ray Sinnema
 * @since 9.10.0
 */
public class CannotReadZipEntryException extends RuntimeException {

  private static final long serialVersionUID = 5528438643799316589L;

  private final File file;

  public CannotReadZipEntryException(Exception e, File file) {
    super("Cannot read file for inclusion into ZIP: " + file, e);
    this.file = file;
  }

  public File getFile() {
    return file;
  }

}
