/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


/**
 *  Build a zip from files on the file system.
 *  @since 19.6.0
 */
public class ZipBuilder {

  private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();

  private final Map<Object, String> entries = new HashMap<>();
  private final String basePath;
  private final Map<String, Integer> numExternalFilesByExtension = new HashMap<>();
  private final ZipCustomization customization;

  public ZipBuilder(File baseDir) {
    this(baseDir, ZipCustomization.none());
  }

  public ZipBuilder(File baseDir, ZipCustomization customization) {
    if (!baseDir.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + baseDir);
    }
    this.basePath = baseDir.getAbsolutePath() + File.separator;
    this.customization = customization;
  }

  /**
   * Add a file to the ZIP.
   * @param file The file to add
   * @return The name of the ZIP entry
   */
  public String add(File file) {
    return add(file, true);
  }

  /**
   * Add a file to the ZIP.
   * @param file The file to add
   * @param preserveExternalFileName Whether external file names are to be preserved as much as possible, or whether
   * they should be replaced with unique numbers
   * @return The name of the ZIP entry
   */
  public String add(File file, boolean preserveExternalFileName) {
    File existingFile = checkFileExists(file);
    String result = zipPathFor(existingFile, preserveExternalFileName);
    entries.put(existingFile, result);
    return result;
  }

  private static File checkFileExists(File file) {
    File result = file.getAbsoluteFile();
    if (!result.isFile()) {
      throw new CannotReadZipEntryException(null, result);
    }
    return result;
  }

  private String zipPathFor(File file, boolean preserveExternalFileName) {
    String result = file.getAbsolutePath();
    if (result.startsWith(basePath)) {
      // Inside base directory => use relative path (which will be unique)
      result = FilenameUtils.separatorsToUnix(result.substring(basePath.length()));
    } else {
      // Outside base directory => ensure unique name
      String ext = FilenameUtils.getExtension(result);
      int index = numExternalFilesByExtension.getOrDefault(ext, 0);
      numExternalFilesByExtension.put(ext, 1 + index);
      if (preserveExternalFileName) {
        result = String.format("%d-%s", index, file.getName());
      } else {
        result = String.format("%d.%s", index, ext);
      }
    }
    return result;
  }

  /**
   * Replace the contents of a file with a different text.
   * @param file The file for which to replace the content
   * @param text The new content to include in the ZIP
   */
  public void replace(File file, String text) {
    replace(file, true, text);
  }

  /**
   * Replace the contents of a file with a different text.
   * @param file The file for which to replace the content
   * @param preserveExternalFileName Whether external file names are to be preserved as much as possible, or whether
   * they should be replaced with unique numbers
   * @param text The new content to include in the ZIP
   */
  public void replace(File file, boolean preserveExternalFileName, String text) {
    String path = entries.containsKey(file) ? entries.remove(file) : zipPathFor(file, preserveExternalFileName);
    entries.put(text, path);
  }

  /**
   * Build a ZIP file containing the added entries.
   * @return A ZIP file containing the added entries
   * @throws IOException In case an I/O error occurs
   */
  public File build() throws IOException {
    if (entries.isEmpty()) {
      throw new EmptyZipException();
    }
    String fileName = "import_configuration" + System.currentTimeMillis() + ".zip";
    File result = new File(TEMP_DIR.toFile(), fileName);

    try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(result))) {
      for (Entry<Object, String> entry : entries.entrySet()) {
        try (InputStream input = toInputStream(entry.getKey())) {
          addEntry(ExtraZipEntry.of(entry.getValue(),
              customization.customize(entry.getValue(),  input)), zip);
        }
      }
      customization.extraEntries().forEach(entry -> addEntry(entry, zip));
      zip.closeEntry();
    }

    return result;
  }

  private InputStream toInputStream(Object source) throws IOException {
    if (source instanceof String) {
      return IOUtils.toInputStream((String)source, StandardCharsets.UTF_8);
    }
    return new FileInputStream((File)source);
  }

  private void addEntry(ExtraZipEntry entry, ZipOutputStream zip) {
    try {
      zip.putNextEntry(new ZipEntry(entry.getName()));
      try (InputStream input = entry.getContent()) {
        IOUtils.copy(input, zip);
      }
    } catch (IOException e) {
      throw new InvalidZipEntryException(entry.getName(), e);
    }
  }

  public ResourceResolver getResourceResolver() {
    return name -> {
      Object source = entries.entrySet().stream()
          .filter(e -> e.getValue().equals(name))
          .map(Entry::getKey)
          .findFirst()
          .orElseThrow(() -> new UnknownResourceException(name, null));
      if (source instanceof String) {
        return (String)source;
      }
      try (InputStream input = toInputStream(source)) {
        return IOUtils.toString(input, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new UnknownResourceException(name, e);
      }
    };
  }

  @Override
  public String toString() {
    return entries.values().toString();
  }

}
