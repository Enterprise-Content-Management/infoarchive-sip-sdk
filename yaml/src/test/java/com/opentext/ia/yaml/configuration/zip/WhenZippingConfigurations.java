/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.core.YamlSyntaxErrorException;

public class WhenZippingConfigurations extends TestCase {

  private static final String INCLUDED_RESOURCE_NAME = "included.txt";
  private static final String CONFIGURATION_FILE_NAME = "configuration.yml";
  private static final String CONFIGURATION_PROPERTIES = "configuration.properties";
  private static final String NAME = "name";
  private static final String CONTENT = "content";
  private static final String RESOURCE = "resource";
  private static final String INCLUDES = "includes";

  @TempDir
  public Path tempFolder;
  private File folder;
  private File yaml;

  @BeforeEach
  public void init() throws IOException {
    folder = newFolder(tempFolder);
  }

  @Test
  public void shouldAddConfigurationFile() throws IOException {
    yaml = yamlFileContaining(new YamlMap());

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(CONFIGURATION_FILE_NAME::equals, zipEntries, "Missing configuration file");
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
    setContent(result, content);
    return result;
  }

  private void setContent(File file, String content) throws IOException {
    if (!file.getParentFile().mkdirs() && !file.getParentFile().isDirectory()) {
      throw new IllegalStateException(
          "Could not create all directories in path " + file.getParentFile().getAbsolutePath());
    }
    FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
  }

  private Map<String, InputStream> zipYaml() throws IOException {
    return new RandomAccessZipFile(ZipConfiguration.of(yaml, new File(EMPTY)));
  }

  private void assertZipEntry(Predicate<String> expected, Map<String, InputStream> actual,
      String message) {
    Set<String> keys = actual.keySet();
    assertTrue(keys.stream().anyMatch(expected), message + ":\n" + keys);
  }

  @Test
  public void shouldAddReferencedResourceFiles() throws IOException {
    File resource = newFile("resource.txt", "ipsum");
    yaml = yamlFileReferencingResource(resource);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(resource.getName()::equals, zipEntries, "Missing external resource");
  }

  private File yamlFileReferencingResource(File resource) throws IOException {
    return yamlFileContaining(new YamlMap().put(someName(),
        new YamlMap().put(CONTENT, new YamlMap().put(RESOURCE, resource.getAbsolutePath()))));
  }

  @Test
  public void shouldAddIncludedYamlFiles() throws IOException {
    File includedYaml = newFile("include.yaml", someYaml().toString());
    yaml = yamlFileIncluding(includedYaml);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(includedYaml.getName()::equals, zipEntries, "Missing included YAML file");
  }

  private YamlMap someYaml() {
    return new YamlMap().put(someName(), someName());
  }

  private String someName() {
    return randomString(5);
  }

  private File yamlFileIncluding(File includedYaml) throws IOException {
    return yamlFileContaining(
        new YamlMap().put(INCLUDES, Collections.singletonList(includedYaml.getAbsolutePath())));
  }

  @Test
  public void shouldAddParentPropertiesFiles() throws IOException {
    File properties = newFile(CONFIGURATION_PROPERTIES, "foo=bar");
    yaml = yamlFileContaining(new YamlMap());

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(properties.getName()::equals, zipEntries, "Missing properties file");
  }

  @Test
  public void shouldNotAllowEmptyZip() throws IOException {
    assertThrows(EmptyZipException.class, () -> zipYaml());
  }

  @Test
  public void shouldNotAllowInvalidYamlInZip() throws IOException {
    yaml = yamlFileContaining("foo: bar: baz");
    InvalidZipEntryException thrown = assertThrows(InvalidZipEntryException.class, () -> zipYaml());
    assertEquals(YamlSyntaxErrorException.class, thrown.getCause().getClass());
  }

  @Test
  public void shouldAvoidNameConflictsWhenIncludingFilesOutsideTheDirectoryTree()
      throws IOException {
    File otherFolder = newFolder(tempFolder);
    File includedFile1 = newFile(otherFolder, INCLUDED_RESOURCE_NAME, "foo");
    File includedFile2 = newFile(new File(otherFolder, "subfolder"), INCLUDED_RESOURCE_NAME, "bar");
    File includedYaml = newFile(otherFolder, "include.yml",
        someYamlReferencing(Arrays.asList(INCLUDED_RESOURCE_NAME, "subfolder/included.txt")));
    String expectedYamlName = "0-" + includedYaml.getName();
    String expectedFileName1 = "0-" + includedFile1.getName();
    String expectedFileName2 = "1-" + includedFile2.getName();
    yaml = yamlFileIncluding(includedYaml);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(expectedYamlName::equals, zipEntries, "Missing included YAML file");
    assertZipEntry(expectedFileName1::equals, zipEntries, "Missing included text file #1");
    assertZipEntry(expectedFileName2::equals, zipEntries, "Missing included text file #2");
    YamlMap zipped = YamlMap.from(zipEntries.get(CONFIGURATION_FILE_NAME));
    assertEquals(expectedYamlName, zipped.get(INCLUDES, 0).toString(), "Included file");
  }

  @Test
  public void shouldPutExternalIncludedYamlNearTheParentWhenSeveralLevelsOfNesting()
      throws IOException {
    File externalFolder = newFolder(tempFolder);
    File externalFile1 = newFile(externalFolder, INCLUDED_RESOURCE_NAME, "foo");
    File externalFile2 = newFile(new File(externalFolder, "subfolder"), INCLUDED_RESOURCE_NAME, "bar");
    File includedExternalYaml = newFile(externalFolder, "included.yml",
        someYamlReferencing(Arrays.asList(INCLUDED_RESOURCE_NAME, "subfolder/included.txt")));

    String expectedYamlName = "0-" + includedExternalYaml.getName();
    String expectedFileName1 = "0-" + externalFile1.getName();
    String expectedFileName2 = "1-" + externalFile2.getName();

    String subfolderName = "sub-folder/";
    String includeNestedPath = subfolderName + CONFIGURATION_FILE_NAME;
    newFile(folder, includeNestedPath,
        new YamlMap().put(INCLUDES,
            Collections.singletonList(includedExternalYaml.getAbsolutePath())).toString());
    String mainYmlFileName = "main.yml";
    yaml = newFile(folder, mainYmlFileName,
        new YamlMap().put(INCLUDES, Collections.singletonList(includeNestedPath)).toString());

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(mainYmlFileName::equals, zipEntries, "Missing main YAML file");
    assertZipEntry(expectedFileName1::equals, zipEntries, "Missing included text file #1");
    assertZipEntry(expectedFileName2::equals, zipEntries, "Missing included text file #2");
    assertZipEntry(includeNestedPath::equals, zipEntries, "Missing included YAML file");
    assertZipEntry((subfolderName + expectedYamlName)::equals,
        zipEntries, "Missing included external YAML file");
    YamlMap zipped = YamlMap.from(zipEntries.get(mainYmlFileName));
    assertEquals(includeNestedPath, zipped.get(INCLUDES, 0).toString(), "Included file");
  }


  private String someYamlReferencing(Collection<String> resources) {
    return new YamlMap().put("pdiSchema", new YamlMap().put(NAME, someName()).put(CONTENT,
        new YamlMap().put("format", "txt").put(RESOURCE, resources))).toString();
  }

  @Test
  public void shouldAddReferencedResourcesUsingWildcards() throws IOException {
    String extension = '.' + randomString(3);
    File resource1 = newFile("resource1" + extension, "ipsum");
    File resource2 = newFile("resource2" + extension, "lorem");
    String pattern = "*" + extension;
    String key = someName();
    yaml = yamlFileContaining(
        new YamlMap().put(key, new YamlMap().put(CONTENT, new YamlMap().put(RESOURCE, pattern))));

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(resource1.getName()::equals, zipEntries, "Missing resource #1");
    assertZipEntry(resource2.getName()::equals, zipEntries, "Missing resource #2");

    YamlMap configuration = YamlMap.from(zipEntries.get(CONFIGURATION_FILE_NAME));
    assertEquals(pattern, configuration.get(key, CONTENT, RESOURCE).toString(), "Pattern");
  }

  @Test
  public void shouldAddReferencedResourcesUsingWildcardsInIncludedYamlFiles() throws IOException {
    File subFolder = newFolder(tempFolder, "include");
    String extension = '.' + randomString(3);
    File resource1 = newFile(subFolder, "resource1" + extension, randomString(13));
    File resource2 = newFile(subFolder, "resource2" + extension, randomString(13));
    File resource3 =
        newFile(subFolder, "resource3.html", "<html><body><h1>Hello</h1></body></html>");
    String pattern = "*" + extension;
    YamlMap includedYaml = new YamlMap()
        .put(someName(), new YamlMap().put(CONTENT, new YamlMap().put(RESOURCE, pattern)))
        .put("customPresentationConfiguration",
            new YamlMap().put(NAME, "Baseball-SearchByPlayerNameView").put("htmlTemplate",
                new YamlMap().put(RESOURCE, resource3.getName())));

    File included = newFile(subFolder, CONFIGURATION_FILE_NAME, includedYaml.toString());
    YamlMap configuration = new YamlMap().put(INCLUDES,
        Collections.singletonList(String.format("%s/%s", subFolder.getName(), included.getName())));
    yaml = Files.createFile(tempFolder.resolve(CONFIGURATION_FILE_NAME)).toFile();
    setContent(yaml, configuration.toString());

    Map<String, InputStream> zipEntries = zipYaml();

    Arrays.asList(resource1, resource2, resource3).forEach(file -> {
      String path = String.format("%s/%s", subFolder.getName(), file.getName());
      assertZipEntry(path::equals, zipEntries, "Missing resource");
    });
  }

  @Test
  public void shouldSubstitutePropertiesToDeterminePathsToResources() throws IOException {
    String text = randomString();
    File resourceFile = newFile(someName() + ".txt", text);
    String resourceFileName = resourceFile.getName();
    newFile(CONFIGURATION_PROPERTIES, "name=" + resourceFileName);
    yaml = newFile(CONFIGURATION_FILE_NAME,
        new YamlMap().put("pdiSchema", new YamlMap().put("name", someName()).put("content",
            new YamlMap().put("format", "txt").put("resource", "${name}"))).toString());

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry(resourceFileName::equals,
        zipEntries, "Resource not included: " + resourceFileName);
  }

  @Test
  public void shouldResolvePathsUpAndDownTheFolderHierarchy() throws IOException {
    File dir = new File("src/test/resources/external-includes/root");
    Map<String, InputStream> zip = new RandomAccessZipFile(ZipConfiguration.of(dir, new File(EMPTY)));

    YamlMap map = YamlMap.from(zip.get(CONFIGURATION_FILE_NAME));
    String zipEntry = map.get(INCLUDES, 0).toString();
    assertTrue(zip.containsKey(zipEntry), "Missing root include: " + zipEntry);

    map = YamlMap.from(zip.get(zipEntry));
    zipEntry = map.get(INCLUDES, 0).toString();
    assertTrue(zip.containsKey(zipEntry), "Missing external include: " + zipEntry);

    map = YamlMap.from(zip.get(zipEntry));
    zipEntry = map.get("pdiSchema", "content", "resource").toString();
    assertTrue(zip.containsKey(zipEntry), "Missing external resource: " + zipEntry);

    assertEquals(zip.entrySet().stream().map(it -> it.getKey()).collect(Collectors.toSet()),
            new HashSet<String>(Arrays.asList("0-configuration.yml",
              "0-pdischema.xsd", "0.properties", "1-configuration.yml",
              "configuration.yml", "configuration.properties")));
  }

  @Test
  public void shouldCustomizeZipConfiguration() throws IOException {
    File dir = new File("src/test/resources/customize");
    String key = someName();
    String value = someName();
    String version = "2.0";
    String entryName = someName();
    String entryContent = someName();
    final AtomicBoolean initialized = new AtomicBoolean();

    Map<String, InputStream> zip = new RandomAccessZipFile(
        ZipConfiguration.of(dir, ZipCustomization.builder().init((names, contentSupplier) -> {
          assertTrue(names.contains(CONFIGURATION_FILE_NAME), "Missing " + CONFIGURATION_FILE_NAME);
          assertTrue(names.contains(CONFIGURATION_PROPERTIES),
              "Missing " + CONFIGURATION_PROPERTIES);
          initialized.set(true);
        }).properties((name, properties) -> properties.setProperty(key, value))
            .yaml((name, map) -> map.put("version", version))
            .extra(() -> ExtraZipEntry.of(entryName, entryContent)).build(), new File[0]));

    assertTrue(initialized.get(), "Not initialized");

    Properties properties = new Properties();
    properties.load(zip.get(CONFIGURATION_PROPERTIES));
    assertEquals(value, properties.getProperty(key), "Value");

    assertEquals(version, YamlMap.from(zip.get(CONFIGURATION_FILE_NAME)).get("version").toString(),
        "Version");

    assertTrue(zip.containsKey(entryName), "Missing extra content");
    assertEquals(entryContent, IOUtils.toString(zip.get(entryName), StandardCharsets.UTF_8),
        "Extra content");
  }

  @Test
  public void shouldZipDirectoryContainingSpaces() throws IOException {
    // Should not throw an exception
    ZipConfiguration.of(configurationWithIncludesInDirectoryWithSpaces(), new File(EMPTY));
  }

  private File configurationWithIncludesInDirectoryWithSpaces() throws IOException {
    File result = new File("build/dir with space/configuration.yml").getCanonicalFile();
    File includeFile = new File(result.getParentFile(), "sub/configuration.yml").getCanonicalFile();
    if (!includeFile.getParentFile().exists()) {
      assertTrue(includeFile.getParentFile().mkdirs(),
          "Failed to create directory: " + includeFile.getParent());
    }
    save(someConfiguration(), includeFile);
    save(configurationWithIncludes(includeFile), result);

    return result;
  }

  private void save(YamlMap configuration, File destination) throws IOException {
    try (OutputStream output = Files.newOutputStream(destination.toPath(), StandardOpenOption.CREATE)) {
      try (InputStream input = configuration.toStream()) {
        IOUtils.copy(input, output);
      }
    }
  }

  private YamlMap configurationWithIncludes(File includedFile) {
    return someConfiguration().put(INCLUDES, Collections.singletonList(includedFile.getPath()));
  }

  private YamlMap someConfiguration() {
    return new YamlMap().put("version", "1.0.0");
  }

}
