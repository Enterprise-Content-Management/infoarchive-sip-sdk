/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


/**
 *  Build a zip from files on the file system.
 *  @since 9.6.0
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
   * @param fileToInclude The file to add
   * @return The name of the ZIP entry
   */
  public String add(File fileToInclude) {
    return add(fileToInclude, null, true);
  }

  /**
   * Add a file to the ZIP.
   * @param fileToInclude The file to add
   * @param yamlFileWithInclude The container yml file, that contains 'includes' statement
   * @return The name of the ZIP entry
   */
  public String add(File fileToInclude, File yamlFileWithInclude) {
    return add(fileToInclude, yamlFileWithInclude, true);
  }

  /**
   * Add a file to the ZIP.
   * @param fileToInclude The file to add
   * @param yamlFileWithInclude The container yml file, that contains 'includes' statement
   * @param preserveExternalFileName Whether external file names are to be preserved as much as possible, or whether
   * they should be replaced with unique numbers
   * @return The name of the ZIP entry
   */
  public String add(File fileToInclude, File yamlFileWithInclude, boolean preserveExternalFileName) {
    File existingFile = checkFileExists(fileToInclude);
    String pathInZip = zipPathFor(existingFile, yamlFileWithInclude, preserveExternalFileName);
    entries.put(existingFile, pathInZip);
    return pathInZip;
  }

  private static File checkFileExists(File file) {
    File result = file.getAbsoluteFile();
    if (!result.isFile()) {
      throw new CannotReadZipEntryException(null, result);
    }
    return result;
  }

  private String zipPathFor(File file, File fileWithInclude,
      boolean preserveExternalFileName) {
    String result = file.getAbsolutePath();
    if (result.startsWith(basePath)) {
      // Inside base directory => use relative path (which will be unique)
      result = FilenameUtils.separatorsToUnix(result.substring(basePath.length()));
    } else {
      // Outside base directory => ensure unique name
      String fileName = "";
      String ext = FilenameUtils.getExtension(result);
      int index = numExternalFilesByExtension.getOrDefault(ext, 0);
      numExternalFilesByExtension.put(ext, 1 + index);
      if (preserveExternalFileName) {
        fileName = String.format("%d-%s", index, file.getName());
      } else {
        fileName = String.format("%d.%s", index, ext);
      }
      result = fileName;

      if (fileWithInclude != null) {
        String rootPath = FilenameUtils.getPath(basePath);
        String fileWithIncludePath = FilenameUtils.getPath(fileWithInclude.getAbsolutePath());
        String directoryForFile = fileWithIncludePath.startsWith(rootPath)
            ? fileWithIncludePath.substring(rootPath.length()) : "";
        result = FilenameUtils.separatorsToUnix(directoryForFile + fileName);
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
    String path = entries.containsKey(file) ? entries.remove(file)
        : zipPathFor(file, null, preserveExternalFileName);
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

    try (ZipOutputStream zip = new ZipOutputStream(
        Files.newOutputStream(result.toPath(), StandardOpenOption.CREATE_NEW))) {
      customization.init(entries.values(), this::streamFor);
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

  private InputStream streamFor(String name) {
    return entries.entrySet().stream()
        .filter(e -> name.equals(e.getValue()))
        .map(Entry::getKey)
        .map(source -> {
          try {
            return toInputStream(source);
          } catch (IOException e1) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private InputStream toInputStream(Object source) throws IOException {
    if (source instanceof String) {
      return IOUtils.toInputStream((String)source, StandardCharsets.UTF_8);
    }
    return Files.newInputStream(((File)source).toPath(), StandardOpenOption.READ);
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
