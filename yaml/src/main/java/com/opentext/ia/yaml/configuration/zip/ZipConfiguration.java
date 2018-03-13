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
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.error.YAMLException;

import com.opentext.ia.yaml.configuration.ConfigurationPropertiesFactory;
import com.opentext.ia.yaml.configuration.ObjectConfiguration;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.FilesSelector;
import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


/**
 * Zip file that holds a YAML configuration for InfoArchive and all the files it references.
 * @author ray
 * @since 9.10.0
 */
public class ZipConfiguration {

  static final String MAIN_YAML_FILE_NAME = "configuration.yml";

  private static final Path TEMP_DIR = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
  private static final int MAX_PARENT_PROPERTIES_FILES_PER_ZIP = 10;
  private static final String TOP_LEVEL_PROPERTIES_FILE_NAME = "default.properties";
  private static final String MAIN_PROPERTIES_FILE_NAME = "configuration.properties";
  private static final String INCLUDES = "includes";
  private static final String RESOURCE = "resource";

  public static File of(File source) throws IOException {
    File yaml = source;
    if (yaml == null) {
      throw new EmptyZipException();
    }
    yaml = yaml.getAbsoluteFile();
    if (yaml.isDirectory()) {
      yaml = new File(yaml, MAIN_YAML_FILE_NAME);
    }
    if (!yaml.isFile()) {
      throw new EmptyZipException();
    }
    return new ZipConfiguration().from(yaml);
  }

  private static void checkFileExists(File file) {
    if (!file.isFile()) {
      throw new CannotReadZipEntryException(null, file);
    }
  }

  private File from(File yaml) throws IOException {
    return zip(yaml, this::addReferencedFilesIn);
  }

  private void addReferencedFilesIn(File file, Map<File, String> filesByPath) {
    YamlMap yaml = addReferencedFilesIn(file, file, new AtomicInteger(), filesByPath);
    try {
      File yamlFile = new File(Files.createTempDirectory("ia-import").toFile(), MAIN_YAML_FILE_NAME);
      copy(yaml.toStream(), yamlFile);
      addPath(yamlFile, yamlFile.getName(), filesByPath);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create temporary directory", e);
    }
  }

  private YamlMap addReferencedFilesIn(File root, File file, AtomicInteger numIncludedFiles,
      Map<File, String> filesByPath) {
    addLocalPropertiesFile(file, filesByPath);
    YamlMap yaml = parseYaml(file);
    addExternalResourceFiles(root, file, yaml, numIncludedFiles, filesByPath);
    addIncludedYamlFiles(root, file, numIncludedFiles, filesByPath, yaml);
    return yaml;
  }

  private void addIncludedYamlFiles(File root, File file, AtomicInteger numIncludedFiles, Map<File, String> filesByPath,
      YamlMap yaml) {
    ListIterator<Value> includes = yaml.get(INCLUDES).toList().listIterator();
    while (includes.hasNext()) {
      Value include = includes.next();
      if (include.isMap()) {
        YamlMap map = include.toMap();
        ObjectConfiguration configuration = ObjectConfiguration.parse(map.get("configure").toString());
        if (configuration.shouldIgnoreObject()) {
          continue;
        }
        include = map.get(RESOURCE);
      }
      File reference = addReferencedFile(root, file, include.toString(), numIncludedFiles, filesByPath);
      includes.set(new Value(filesByPath.get(reference)));
    }
  }

  private void addExternalResourceFiles(File root, File file, YamlMap yaml, AtomicInteger numIncludedFiles,
      Map<File, String> filesByPath) {
    yaml.visit(visit -> {
      YamlMap map = visit.getMap();
      if (map.containsKey(RESOURCE)) {
        List<File> resolvedFiles = new FilesSelector(file.getParentFile()).apply(map.get(RESOURCE).toString());
        if (resolvedFiles.size() == 1) {
          File reference = addReferencedFile(root, file, resolvedFiles.get(0).getAbsolutePath(), numIncludedFiles,
              filesByPath);
          map.put(RESOURCE, filesByPath.get(reference));
        } else {
          resolvedFiles.forEach(f ->
              addReferencedFile(root, f, f.getAbsolutePath(), numIncludedFiles, filesByPath));
          // Leave pattern as-is
        }
      }
    });
  }

  private YamlMap parseYaml(File file) {
    YamlMap yaml;
    try {
      yaml = YamlMap.from(file);
    } catch (IOException | YAMLException e) {
      throw new InvalidZipEntryException(file, e);
    }
    return yaml;
  }

  private void addLocalPropertiesFile(File file, Map<File, String> filesByPath) {
    if (MAIN_YAML_FILE_NAME.equals(file.getName())) {
      File propertiesFile = new File(file.getParentFile(), MAIN_PROPERTIES_FILE_NAME);
      if (propertiesFile.isFile()) {
        addPath(propertiesFile, propertiesFile.getName(), filesByPath);
      }
    }
  }

  private void copy(InputStream source, File destination) {
    try (InputStream input = source) {
      try (OutputStream output = new FileOutputStream(destination)) {
        IOUtils.copy(input, output);
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to", e);
    }
  }

  private File addReferencedFile(File root, File source, String reference, AtomicInteger numIncludedFiles,
      Map<File, String> filesByPath) {
    String path = uriToPath(source.toURI().resolve(toRelativeUri(reference, filesByPath)));
    File result = new File(path);
    path = getRelativePath(root, path)
        .orElseGet(() -> String.format("%d-%s", numIncludedFiles.getAndIncrement(), result.getName()));
    addPath(result, path, filesByPath);
    if (FilenameUtils.isExtension(path, "yml")) {
      addReferencedFilesIn(root, result, numIncludedFiles, filesByPath);
    }
    return result.getAbsoluteFile();
  }

  private String uriToPath(URI uri) {
    return uri.toString().substring(5); // After "file:"
  }

  private String toRelativeUri(String reference, Map<File, String> files) {
    String result = toUnix(reference);
    ResourceResolver resourceResolver = name -> files.entrySet().stream()
          .filter(e -> e.getValue().equals(name))
          .map(Entry::getKey)
          .findAny()
          .map(this::contentsOf)
          .orElseThrow(() -> new UnknownResourceException(name, null));
    return ConfigurationPropertiesFactory.newInstance(resourceResolver).apply(result);
  }

  private String contentsOf(File file) {
    try (InputStream input = new FileInputStream(file)) {
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UnknownResourceException(file.getName(), e);
    }
  }

  private void addPath(File file, String path, Map<File, String> filesByPath) {
    filesByPath.putIfAbsent(file.getAbsoluteFile(), toUnix(path));
  }

  private Optional<String> getRelativePath(File source, String path) {
    try {
      String sourcePath = source.getParentFile().getCanonicalPath() + File.separator;
      String otherPath = new File(path).getCanonicalPath();
      return otherPath.startsWith(sourcePath)
          ? Optional.of(otherPath.substring(sourcePath.length()))
          : Optional.empty();
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  private String toUnix(String path) {
    String result = FilenameUtils.separatorsToUnix(path);
    int prefixLength = FilenameUtils.getPrefixLength(result);
    return prefixLength <= 1 ? result : result.substring(prefixLength - 1);
  }

  private File zip(File file, BiConsumer<File, Map<File, String>> fileAdder) throws IOException {
    return createZipArchive(collectFilesToZip(file, fileAdder));
  }

  private Map<File, String> collectFilesToZip(File base, BiConsumer<File, Map<File, String>> fileAdder)
      throws EmptyZipException {
    Map<File, String> result = new HashMap<>();
    addParentPropertiesFiles(base.getParentFile(), result);
    fileAdder.accept(base, result);
    if (result.isEmpty()) {
      throw new EmptyZipException();
    }
    return result;
  }

  private void addParentPropertiesFiles(File directory, Map<File, String> filesByPath) {
    List<File> propertiesFiles = new ArrayList<>();
    File currentDir = new File(directory.getAbsolutePath()).getParentFile();
    while (currentDir != null) {
      final File[] files = currentDir.listFiles();
      if (files != null) {
        Arrays.stream(files).filter(file -> file.getName().endsWith(".properties"))
            .forEach(file -> propertiesFiles.add(0, file));
      }
      if (hasReachedTopDirectoryOrMaximumNumberOfFiles(propertiesFiles)) {
        break;
      }
      currentDir = currentDir.getParentFile();
    }
    for (int i = 0; i < propertiesFiles.size(); i++) {
      addPath(propertiesFiles.get(i), String.format("%d.properties", i), filesByPath);
    }
  }

  private boolean hasReachedTopDirectoryOrMaximumNumberOfFiles(List<File> propertiesFiles) {
    return propertiesFiles.size() >= MAX_PARENT_PROPERTIES_FILES_PER_ZIP
        || propertiesFiles.stream().map(File::getName).anyMatch(TOP_LEVEL_PROPERTIES_FILE_NAME::equals);
  }

  private File createZipArchive(Map<File, String> filesByName) throws IOException {
    if (filesByName.isEmpty()) {
      throw new EmptyZipException();
    }
    String fileName = "import_configuration" + System.currentTimeMillis() + ".zip";
    File result = new File(TEMP_DIR.toFile(), fileName);

    try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(result))) {
      for (Entry<File, String> entry : filesByName.entrySet()) {
        String zipFilePath = entry.getValue();
        File file = checkFileExists(entry.getKey().getAbsolutePath());
        try (InputStream in = new FileInputStream(file)) {
          ZipEntry fileEntry = new ZipEntry(zipFilePath);
          zip.putNextEntry(fileEntry);
          IOUtils.copy(in, zip);
          zip.closeEntry();
        }
      }
    }

    return result;
  }

  private static File checkFileExists(String path) {
    File result = new File(path);
    checkFileExists(result);
    return result;
  }

}
