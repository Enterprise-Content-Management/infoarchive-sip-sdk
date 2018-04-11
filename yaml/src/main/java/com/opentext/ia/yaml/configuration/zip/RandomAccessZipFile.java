/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;


/**
 * In-memory map representation of a ZIP file, where the keys correspond to the names of the ZIP entries and the
 * values to their content. The content streams can be read multiple times, because they are stored in memory.
 * @author Ray Sinnema
 * @since 9.10.0
 */
public class RandomAccessZipFile extends HashMap<String, InputStream> {

  public RandomAccessZipFile(File input) throws IOException {
    this(new FileInputStream(input));
  }

  public RandomAccessZipFile(InputStream input) throws IOException {
    try (ZipInputStream zip = new ZipInputStream(input)) {
      for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        put(entry.getName(), copy(zip));
      }
    }
  }

  private InputStream copy(InputStream input) throws IOException {
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      IOUtils.copy(input, output);
      InputStream result = new ByteArrayInputStream(output.toByteArray());
      result.mark(-1);
      return result;
    }
  }

  @Override
  public InputStream get(Object key) {
    InputStream result = super.get(key);
    if (result != null) {
      try {
        result.reset();
      } catch (IOException e) {
        // Won't happen with in-memory manipulations
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return entrySet().stream()
        .map(e -> String.format("%s=%s", e.getKey(), toString(e.getValue())))
        .collect(Collectors.joining(System.lineSeparator()));
  }

  private String toString(InputStream input) {
    try {
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return "Error serializing " + input;
    }
  }

}
