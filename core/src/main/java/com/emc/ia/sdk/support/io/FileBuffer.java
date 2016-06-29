/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;


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
      return Files.createTempFile(null, null).toFile();
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
    return new FileOutputStream(file);
  }

  @Override
  public InputStream openForReading() throws IOException {
    return new FileInputStream(file);
  }

  @Override
  public long length() {
    return file.length();
  }

}
