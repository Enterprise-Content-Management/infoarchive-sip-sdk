/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.error.YAMLException;

import com.opentext.ia.yaml.configuration.ConfigurationPropertiesFactory;
import com.opentext.ia.yaml.configuration.ObjectConfiguration;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.FilesSelector;


/**
 * Zip file that holds a YAML configuration for InfoArchive and all the files it references.
 * @author Ray Sinnema
 * @since 9.10.0
 */
public class ZipConfiguration {

  static final String MAIN_YAML_FILE_NAME = "configuration.yml";

  private static final int MAX_PARENT_PROPERTIES_FILES_PER_ZIP = 10;
  private static final String TOP_LEVEL_PROPERTIES_FILE_NAME = "default.properties";
  private static final String MAIN_PROPERTIES_FILE_NAME = "configuration.properties";
  private static final Collection<String> PROPERTY_FILE_NAMES = Arrays.asList(
      TOP_LEVEL_PROPERTIES_FILE_NAME, MAIN_PROPERTIES_FILE_NAME);
  private static final String INCLUDES = "includes";
  private static final String RESOURCE = "resource";

  public static File of(File source) throws IOException {
    return of(source, ZipCustomization.none());
  }

  public static File of(File source, ZipCustomization customization) throws IOException {
    File yaml = source;
    if (yaml == null) {
      throw new EmptyZipException();
    }
    yaml = yaml.getCanonicalFile();
    if (yaml.isDirectory()) {
      yaml = new File(yaml, MAIN_YAML_FILE_NAME);
    }
    if (!yaml.isFile()) {
      throw new EmptyZipException();
    }
    return new ZipConfiguration(yaml, customization).build();
  }

  private final ZipBuilder builder;
  private final File root;

  public ZipConfiguration(File yaml) {
    this(yaml, ZipCustomization.none());
  }

  public ZipConfiguration(File yaml, ZipCustomization customization) {
    this.root = yaml.getAbsoluteFile();
    builder = new ZipBuilder(yaml.getParentFile(), customization);
  }

  private File build() throws IOException {
    addParentPropertiesFiles(root.getParentFile());
    builder.add(root);
    addFromYaml(root);
    return builder.build();
  }

  private void addParentPropertiesFiles(File directory) {
    List<File> propertiesFiles = new ArrayList<>();
    File currentDir = new File(directory.getAbsolutePath()).getParentFile();
    while (currentDir != null) {
      final File[] files = currentDir.listFiles();
      if (files != null) {
        Arrays.stream(files)
            .filter(file -> PROPERTY_FILE_NAMES.contains(file.getName()))
            .forEach(propertiesFiles::add);
      }
      if (hasReachedTopDirectoryOrMaximumNumberOfFiles(propertiesFiles)) {
        break;
      }
      currentDir = currentDir.getParentFile();
    }
    propertiesFiles.forEach(file -> builder.add(file, null, false));
  }

  private boolean hasReachedTopDirectoryOrMaximumNumberOfFiles(List<File> propertiesFiles) {
    return propertiesFiles.size() >= MAX_PARENT_PROPERTIES_FILES_PER_ZIP
        || propertiesFiles.stream().map(File::getName).anyMatch(TOP_LEVEL_PROPERTIES_FILE_NAME::equals);
  }

 private void addFromYaml(File file) throws IOException {
    addLocalPropertiesFile(file);
    YamlMap yaml = parseYaml(file);
    addIncludedYamlFiles(file, yaml);
    addExternalResourceFiles(file, yaml);
    builder.replace(file, yaml.toString());
  }

  private YamlMap parseYaml(File file) {
    try {
      return YamlMap.from(file);
    } catch (IOException | YAMLException e) {
      throw new InvalidZipEntryException(file, e);
    }
  }

  private void addLocalPropertiesFile(File file) {
    File propertiesFile = new File(file.getParentFile(), MAIN_PROPERTIES_FILE_NAME);
    if (propertiesFile.isFile()) {
      builder.add(propertiesFile);
    }
  }

  private void addIncludedYamlFiles(File base, YamlMap yaml) throws IOException {
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
      MappedFile mappedFile = mapFile(base, include);
      includes.set(new Value(mappedFile.path));
      addFromYaml(mappedFile.file);
    }
  }

  private MappedFile mapFile(File base, Value value) throws IOException {
    String pathInYaml = value.toString();
    String pathAfterPropertySubstitution = substituteProperties(pathInYaml);
    File file = resolveFile(base, pathAfterPropertySubstitution);
    return mapFile(pathAfterPropertySubstitution, file, base);
  }

  private MappedFile mapFile(String pathAfterPropertySubstitution,
      File file, File yamlFileWithInclude) {
    String pathInZip = builder.add(file, yamlFileWithInclude);
    String path;
    if (file.getPath().startsWith(root.getParent() + File.separator)) {
      // Included file is in same directory as YAML file or lower => leave the path as is
      path = pathAfterPropertySubstitution;
    } else {
      // Included file is in different location => use the modified path in the ZIP
      path = FilenameUtils.getName(pathInZip);
    }
    return new MappedFile(file, path);
  }

  private String substituteProperties(String value) {
    return ConfigurationPropertiesFactory.newInstance(builder.getResourceResolver()).apply(value);
  }

  private File resolveFile(File base, String path) throws IOException {
    URI baseUri = base.getParentFile().toURI();
    String result = toUnix(path).replace(" ", "%20");
    result = baseUri.resolve(result).toString().substring(5); // After "file:"
    result = URLDecoder.decode(result, StandardCharsets.UTF_8.name());
    return new File(result).getCanonicalFile();
  }

  private String toUnix(String path) {
    String result = FilenameUtils.separatorsToUnix(path);
    int prefixLength = FilenameUtils.getPrefixLength(result);
    return prefixLength <= 1 ? result : result.substring(prefixLength - 1);
  }

  private void addExternalResourceFiles(File base, YamlMap yaml) {
    yaml.visit(visit -> {
      YamlMap map = visit.getMap();
      if (map.containsKey(RESOURCE)) {
        Value resource = map.get(RESOURCE);
        if (resource.isList()) {
          ListIterator<Value> iterator = resource.toList().listIterator();
          while (iterator.hasNext()) {
            addExternalResourceFile(base, iterator.next(), path -> iterator.set(new Value(path)));
          }
        } else {
          addExternalResourceFile(base, map.get(RESOURCE), path -> map.put(RESOURCE, path));
        }
      }
    });
  }

  private void addExternalResourceFile(File base, Value resourcePattern, Consumer<String> updateYaml) {
    String pathInYaml = resourcePattern.toString();
    String pathAfterPropertySubstitution = substituteProperties(pathInYaml);
    List<File> files = new FilesSelector(base.getParentFile()).apply(pathAfterPropertySubstitution);
    if (files.size() == 1) {
      String newPathInYaml = mapFile(pathAfterPropertySubstitution, files.get(0), base).path;
      updateYaml.accept(newPathInYaml);
    } else {
      files.forEach(builder::add);
      // Leave pattern as-is in YAML
    }
  }


  private static class MappedFile {

    private final File file;
    private final String path;

    MappedFile(File file, String path) {
      this.file = file;
      this.path = path;
    }

  }

}
