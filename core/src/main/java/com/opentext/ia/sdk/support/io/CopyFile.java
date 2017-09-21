/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 * Copy a file to a destination directory.
 */
public final class CopyFile {

  /**
   * Copy a file from a source.
   * @param source The source file to copy
   * @return An instance specific to the provided source
   */
  public static CopyFile from(File source) {
    return new CopyFile(source);
  }

  private final File source;

  /**
   * Copy a file from a source.
   * @param source The source file to copy
   */
  public CopyFile(File source) {
    this.source = source;
  }

  /**
   * Copy the source to a given directory.
   * @param destinationDir The directory to copy the source into
   */
  public void to(File destinationDir) {
    copyToFile(new File(destinationDir, source.getName()));
  }

  private void copyToFile(File destination) {
    try (InputStream src = new FileInputStream(source)) {
      try (OutputStream dst = new FileOutputStream(destination)) {
        IOUtils.copy(src, dst);
      }
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

}
