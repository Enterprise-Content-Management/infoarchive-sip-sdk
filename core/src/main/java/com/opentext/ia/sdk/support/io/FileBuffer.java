/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Buffer that stores data in a file.
 */
public class FileBuffer implements DataBuffer {

  private final File file;

  /**
   * Store data in a temporary file.
   */
  public FileBuffer() {
    this(tempFile());
  }

  private static File tempFile() {
    try {
      File tempFile = Files.createTempFile(null, null).toFile();
      tempFile.deleteOnExit();
      return tempFile;
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  /**
   * Store data in the given file.
   * @param file The file in which to store data
   */
  public FileBuffer(File file) {
    this.file = file;
  }

  @Override
  public OutputStream openForWriting() throws IOException {
    return Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE);
  }

  @Override
  public InputStream openForReading() throws IOException {
    return Files.newInputStream(file.toPath(), StandardOpenOption.READ);
  }

  @Override
  public long length() {
    return file.length();
  }

}
