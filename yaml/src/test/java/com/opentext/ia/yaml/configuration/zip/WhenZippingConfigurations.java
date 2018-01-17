/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.opentext.ia.yaml.core.YamlMap;


public class WhenZippingConfigurations {

  private static final String CONFIGURATION_FILE_NAME = "configuration.yml";

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();
  private File folder;
  private File yaml;

  @Before
  public void init() throws IOException {
    folder = tempFolder.newFolder();
  }

  @Test
  public void shouldAddConfigurationFile() throws IOException {
    yaml = yamlFileContaining(new YamlMap());

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntries("Missing configuration file", CONFIGURATION_FILE_NAME::equals, zipEntries);
  }

  private File yamlFileContaining(YamlMap content) throws IOException {
    return yamlFileContaining(content.toString());
  }

  private File yamlFileContaining(String content) throws IOException {
    return newFile(CONFIGURATION_FILE_NAME, content);
  }

  private File newFile(String name, String content) throws IOException {
    return newFile(folder, name, content);
  }

  private File newFile(File dir, String name, String content) throws IOException {
    File result = new File(dir, name);
    try (OutputStream output = new FileOutputStream(result)) {
      try (Reader input = new StringReader(content)) {
        IOUtils.copy(input, output, StandardCharsets.UTF_8);
      }
    }
    return result;
  }

  private Map<String, InputStream> zipYaml() throws IOException {
    return new RandomAccessZipFile(ZipConfiguration.of(yaml));
  }

  private void assertZipEntries(String message, Predicate<String> expected, Map<String, InputStream> actual) {
    assertTrue(message, actual.keySet().stream().anyMatch(expected));
  }

  @Test
  public void shouldAddReferencedResourceFiles() throws IOException {
    File resource = newFile("resource.txt", "ipsum");
    yaml = yamlFileReferencingResource(resource);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntries("Missing external resource", resource.getName()::equals, zipEntries);
  }

  private File yamlFileReferencingResource(File resource) throws IOException {
    return yamlFileContaining(new YamlMap()
        .put("foo", new YamlMap()
            .put("content", new YamlMap()
                .put("resource", resource.getAbsolutePath()))));
  }

  @Test
  public void shouldAddIncludedYamlFiles() throws IOException {
    File includedYaml = newFile("include.yaml", someYaml().toString());
    yaml = yamlFileIncluding(includedYaml);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntries("Missing included YAML file", includedYaml.getName()::equals, zipEntries);
  }

  private YamlMap someYaml() {
    return new YamlMap().put("foo", "bar");
  }

  private File yamlFileIncluding(File includedYaml) throws IOException {
    return yamlFileContaining(new YamlMap()
        .put("includes", Arrays.asList(includedYaml.getAbsolutePath())));
  }

  @Test
  public void shouldAddParentPropertiesFiles() throws IOException {
    File properties = newFile("configuration.properties", "foo=bar");
    yaml = yamlFileContaining(new YamlMap());

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntries("Missing properties file", properties.getName()::equals, zipEntries);
  }

  @Test(expected = EmptyZipException.class)
  public void shouldNotAllowEmptyZip() throws IOException {
    zipYaml();
  }

  @Test(expected = InvalidZipEntryException.class)
  public void shouldNotAllowInvalidYamlInZip() throws IOException {
    yaml = yamlFileContaining("foo: bar: baz");

    zipYaml();
  }

  @Test
  public void shouldAvoidNameConflictsWhenIncludingFilesOutsideTheDirectoryTree() throws IOException {
    File includedYaml = newFile(tempFolder.newFolder(), "include.yaml", someYaml().toString());
    String expectedName = "0-" + includedYaml.getName();
    yaml = yamlFileIncluding(includedYaml);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntries("Missing included YAML file", expectedName::equals, zipEntries);
  }

}
