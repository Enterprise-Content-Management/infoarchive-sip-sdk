/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.zip.ZipFile;

/**
 * Process a named entry in a ZIP file.
 */
public class Unzip {

  /**
   * Process a named entry in a given ZIP file.
   * @param file The ZIP file to process
   * @return A new instance for processing the given ZIP file
   */
  public static Unzip file(File file) {
    return new Unzip(file);
  }

  private final File file;

  /**
   * Process a named entry in a given ZIP file.
   * @param file The ZIP file to process
   */
  public Unzip(File file) {
    this.file = file;
  }

  /**
   * Process a named entry in the ZIP file.
   * @param <T> The type of result returned by the processor
   * @param entry The name of the entry to process
   * @param processor The processor that handles the entry's content
   * @return The result of processing the entry
   */
  public <T> T andProcessEntry(String entry, Function<InputStream, T> processor) {
    try (ZipFile zipFile = new ZipFile(file)) {
      return processEntry(zipFile, entry, processor);
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    }
  }

  private <T> T processEntry(ZipFile zipFile, String entry, Function<InputStream, T> processor) throws IOException {
    try (InputStream stream = zipFile.getInputStream(zipFile.getEntry(entry))) {
      return processor.apply(stream);
    }
  }

}
