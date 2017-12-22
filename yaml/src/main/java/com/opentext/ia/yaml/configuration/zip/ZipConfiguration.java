/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.error.YAMLException;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;


/**
 * Zip file that holds a YAML configuration for InfoArchive and all the files it references.
 * @author ray
 * @since 9.10.0
 */
public class ZipConfiguration {

  private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
  private static final int MAX_PARENT_PROPERTIES_FILES_PER_ZIP = 10;
  private static final List<String> YAML_FILE_EXTENSIONS = Arrays.asList("yml", "yaml");
  private static final String CONFIGURATION_FILE_NAME = "configuration.yml";
  private static final String LOCAL_PROPERTIES_FILE_NAME = "configuration.properties";
  private static final String TOP_LEVEL_PROPERTIES_FILE_NAME = "default.properties";
  private static final String RESOURCE_KEY = "resource";
  private static final String INCLUDES_KEY = "includes";

  private final Map<File, String> entries = new HashMap<>();
  private final File root;
  private final String rootPath;
  private int numExternalEntries;

  public static File of(File yaml) throws IOException {
    return new ZipConfiguration(yaml).toZip();
  }

  public ZipConfiguration(File yaml) throws IOException {
    this.root = Optional.ofNullable(yaml)
        .orElseThrow(EmptyZipException::new)
        .getCanonicalFile();
    this.rootPath = this.root.getParentFile().getPath() + File.separator;
}

  private File toZip() throws IOException {
    collectEntries();
    return zipEntries();
  }

  private void collectEntries() throws IOException {
    addReferencedFiles();
    addParentPropertiesFiles();
  }

  private void addReferencedFiles() throws IOException {
    YamlMap updatedYaml = addReferencedFilesIn(root);
    File updatedYamlFile = new File(Files.createTempDirectory("ia-configuration").toFile(), CONFIGURATION_FILE_NAME);
    copy(updatedYaml.toStream(), updatedYamlFile);
    addPath(updatedYamlFile);
  }

  private YamlMap addReferencedFilesIn(File yaml) {
    YamlMap result = parse(yaml);
    addReferencedResourceFiles(yaml, result);
    addIncludedYamlFiles(yaml, result);
    addLocalPropertiesFile(yaml);
    return result;
  }

  private YamlMap parse(File yaml) {
    try {
      return YamlMap.from(yaml);
    } catch (IOException | YAMLException e) {
      throw new CannotReadZipEntryException(e, yaml);
    }
  }

  private void addReferencedResourceFiles(File base, YamlMap yaml) {
    yaml.visit(visit -> {
      YamlMap map = visit.getMap();
      if (map.containsKey(RESOURCE_KEY)) {
        File reference = addReferencedFile(base, map.get(RESOURCE_KEY));
        map.put(RESOURCE_KEY, entries.get(reference));
      }
    });
  }

  private void addIncludedYamlFiles(File base, YamlMap yaml) {
    ListIterator<Value> includes = yaml.get(INCLUDES_KEY).toList().listIterator();
    while (includes.hasNext()) {
      File reference = addReferencedFile(base, includes.next());
      includes.set(new Value(entries.get(reference)));
    }
  }

  private void addLocalPropertiesFile(File file) {
    Optional.of(file.getName())
        .filter(CONFIGURATION_FILE_NAME::equals)
        .map(ignored -> new File(file.getParentFile(), LOCAL_PROPERTIES_FILE_NAME))
        .filter(File::isFile)
        .ifPresent(this::addPath);
  }

  private File addReferencedFile(File base, Value reference) {
    File result = resolve(base, reference);
    String path = makeRelative(result).orElseGet(() -> pathForExternalEntry(result));
    addPath(result, path);
    if (isYamlFile(path)) {
      addReferencedFilesIn(result);
    }
    return result;
  }

  private File resolve(File base, Value reference) {
    return new File(resolve(base, reference.toString())).getAbsoluteFile();
  }

  private String resolve(File base, String reference) {
    return base.toURI().resolve(toUnix(reference)).toString().substring(5); // After "file:"
  }

  private String toUnix(String path) {
    return stripWindowsDrive(path.replace(File.separator, "/"));
  }

  private String stripWindowsDrive(String path) {
    if (path.matches("[A-Z]:/.*")) {
      return path.substring(2);
    }
    return path;
  }

  private Optional<String> makeRelative(File path) {
    try {
      String otherPath = path.getCanonicalPath();
      return otherPath.startsWith(rootPath)
          ? Optional.of(otherPath.substring(rootPath.length()))
          : Optional.empty();
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  private String pathForExternalEntry(File entry) {
    return String.format("%d-%s", numExternalEntries++, entry.getName());
  }

  private void addPath(File file) {
    addPath(file, file.getName());
  }

  private void addPath(File file, String path) {
    entries.putIfAbsent(file.getAbsoluteFile(), toUnix(path));
  }

  private boolean isYamlFile(String path) {
    return YAML_FILE_EXTENSIONS.contains(FilenameUtils.getExtension(path));
  }

  private void copy(InputStream source, File destination) throws IOException {
    try (InputStream input = source) {
      try (OutputStream output = new FileOutputStream(destination)) {
        IOUtils.copy(input, output);
      }
    }
  }

  private void addParentPropertiesFiles() {
    List<File> propertiesFiles = new ArrayList<>();
    File currentDir = new File(root.getAbsolutePath()).getParentFile();
    while (currentDir != null) {
      addPropertiesFilesIn(currentDir, propertiesFiles);
      if (hasAllPropertiesFiles(propertiesFiles)) {
        break;
      }
      currentDir = currentDir.getParentFile();
    }
    for (int i = 0; i < propertiesFiles.size(); i++) {
      addPath(propertiesFiles.get(i), String.format("%d.properties", i));
    }
  }

  private void addPropertiesFilesIn(File dir, List<File> propertiesFiles) {
    filesIn(dir)
        .filter(file -> "properties".equals(FilenameUtils.getExtension(file.getName())))
        .forEach(file -> propertiesFiles.add(0, file));
  }

  private Stream<File> filesIn(File dir) {
    return Optional.ofNullable(dir.listFiles())
        .map(Arrays::stream)
        .orElse(Stream.empty());
  }

  private boolean hasAllPropertiesFiles(Collection<File> propertiesFiles) {
    return hasMaximumPropertiesFiles(propertiesFiles) || hasTopLevelPropertiesFile(propertiesFiles);
  }

  private boolean hasMaximumPropertiesFiles(Collection<File> propertiesFiles) {
    return propertiesFiles.size() >= MAX_PARENT_PROPERTIES_FILES_PER_ZIP;
  }

  private boolean hasTopLevelPropertiesFile(Collection<File> propertiesFiles) {
    return propertiesFiles.stream()
        .map(File::getName)
        .anyMatch(TOP_LEVEL_PROPERTIES_FILE_NAME::equals);
  }

  private File zipEntries() throws IOException {
    File result = new File(TEMP_DIR.toFile(), "import_configuration_" + OffsetDateTime.now() + ".zip");
    try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(result))) {
      for (Entry<File, String> entry : entries.entrySet()) {
        addEntry(entry.getValue(), entry.getKey(), zip);
      }
    }
    return result;
  }

  private void addEntry(String name, File source, ZipOutputStream zip) throws IOException {
    try (InputStream input = new FileInputStream(source)) {
      zip.putNextEntry(new ZipEntry(name));
      IOUtils.copy(input, zip);
      zip.closeEntry();
    }
  }

}
