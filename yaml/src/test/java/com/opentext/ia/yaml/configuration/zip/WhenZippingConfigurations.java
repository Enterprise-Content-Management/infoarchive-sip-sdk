/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

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

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();
  @Rule
  public ExpectedException thrown = ExpectedException.none();
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

    assertZipEntry("Missing configuration file", CONFIGURATION_FILE_NAME::equals, zipEntries);
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

  private void assertZipEntry(String message, Predicate<String> expected,
      Map<String, InputStream> actual) {
    Set<String> keys = actual.keySet();
    assertTrue(message + ":\n" + keys, keys.stream().anyMatch(expected));
  }

  @Test
  public void shouldAddReferencedResourceFiles() throws IOException {
    File resource = newFile("resource.txt", "ipsum");
    yaml = yamlFileReferencingResource(resource);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry("Missing external resource", resource.getName()::equals, zipEntries);
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

    assertZipEntry("Missing included YAML file", includedYaml.getName()::equals, zipEntries);
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

    assertZipEntry("Missing properties file", properties.getName()::equals, zipEntries);
  }

  @Test(expected = EmptyZipException.class)
  public void shouldNotAllowEmptyZip() throws IOException {
    zipYaml();
  }

  @Test
  public void shouldNotAllowInvalidYamlInZip() throws IOException {
    thrown.expect(InvalidZipEntryException.class);
    thrown.expectCause(instanceOf(YamlSyntaxErrorException.class)); // Provide uniform interface to YAML syntax errors

    yaml = yamlFileContaining("foo: bar: baz");

    zipYaml();
  }

  @Test
  public void shouldAvoidNameConflictsWhenIncludingFilesOutsideTheDirectoryTree()
      throws IOException {
    File otherFolder = tempFolder.newFolder();
    File includedFile1 = newFile(otherFolder, INCLUDED_RESOURCE_NAME, "foo");
    File includedFile2 = newFile(new File(otherFolder, "subfolder"), INCLUDED_RESOURCE_NAME, "bar");
    File includedYaml = newFile(otherFolder, "include.yml",
        someYamlReferencing(Arrays.asList(INCLUDED_RESOURCE_NAME, "subfolder/included.txt")));
    String expectedYamlName = "0-" + includedYaml.getName();
    String expectedFileName1 = "0-" + includedFile1.getName();
    String expectedFileName2 = "1-" + includedFile2.getName();
    yaml = yamlFileIncluding(includedYaml);

    Map<String, InputStream> zipEntries = zipYaml();

    assertZipEntry("Missing included YAML file", expectedYamlName::equals, zipEntries);
    assertZipEntry("Missing included text file #1", expectedFileName1::equals, zipEntries);
    assertZipEntry("Missing included text file #2", expectedFileName2::equals, zipEntries);
    YamlMap zipped = YamlMap.from(zipEntries.get(CONFIGURATION_FILE_NAME));
    assertEquals("Included file", expectedYamlName, zipped.get(INCLUDES, 0).toString());
  }

  @Test
  public void shouldPutExternalIncludedYamlNearTheParentWhenSeveralLevelsOfNesting()
      throws IOException {
    File externalFolder = tempFolder.newFolder();
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

    assertZipEntry("Missing main YAML file", mainYmlFileName::equals, zipEntries);
    assertZipEntry("Missing included text file #1", expectedFileName1::equals, zipEntries);
    assertZipEntry("Missing included text file #2", expectedFileName2::equals, zipEntries);
    assertZipEntry("Missing included YAML file", includeNestedPath::equals, zipEntries);
    assertZipEntry("Missing included external YAML file",
        (subfolderName + expectedYamlName)::equals, zipEntries);
    YamlMap zipped = YamlMap.from(zipEntries.get(mainYmlFileName));
    assertEquals("Included file", includeNestedPath, zipped.get(INCLUDES, 0).toString());
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

    assertZipEntry("Missing resource #1", resource1.getName()::equals, zipEntries);
    assertZipEntry("Missing resource #2", resource2.getName()::equals, zipEntries);

    YamlMap configuration = YamlMap.from(zipEntries.get(CONFIGURATION_FILE_NAME));
    assertEquals("Pattern", pattern, configuration.get(key, CONTENT, RESOURCE).toString());
  }

  @Test
  public void shouldAddReferencedResourcesUsingWildcardsInIncludedYamlFiles() throws IOException {
    File subFolder = tempFolder.newFolder("include");
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
    yaml = tempFolder.newFile(CONFIGURATION_FILE_NAME);
    setContent(yaml, configuration.toString());

    Map<String, InputStream> zipEntries = zipYaml();

    Arrays.asList(resource1, resource2, resource3).forEach(file -> {
      String path = String.format("%s/%s", subFolder.getName(), file.getName());
      assertZipEntry("Missing resource", path::equals, zipEntries);
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

    assertZipEntry("Resource not included: " + resourceFileName,
        resourceFileName::equals, zipEntries);
  }

  @Test
  public void shouldResolvePathsUpAndDownTheFolderHierarchy() throws IOException {
    File dir = new File("src/test/resources/external-includes/root");
    Map<String, InputStream> zip = new RandomAccessZipFile(ZipConfiguration.of(dir, new File(EMPTY)));

    YamlMap map = YamlMap.from(zip.get(CONFIGURATION_FILE_NAME));
    String zipEntry = map.get(INCLUDES, 0).toString();
    assertTrue("Missing root include: " + zipEntry, zip.containsKey(zipEntry));

    map = YamlMap.from(zip.get(zipEntry));
    zipEntry = map.get(INCLUDES, 0).toString();
    assertTrue("Missing external include: " + zipEntry, zip.containsKey(zipEntry));

    map = YamlMap.from(zip.get(zipEntry));
    zipEntry = map.get("pdiSchema", "content", "resource").toString();
    assertTrue("Missing external resource: " + zipEntry, zip.containsKey(zipEntry));
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
          assertTrue("Missing " + CONFIGURATION_FILE_NAME, names.contains(CONFIGURATION_FILE_NAME));
          assertTrue("Missing " + CONFIGURATION_PROPERTIES,
              names.contains(CONFIGURATION_PROPERTIES));
          initialized.set(true);
        }).properties((name, properties) -> properties.setProperty(key, value))
            .yaml((name, map) -> map.put("version", version))
            .extra(() -> ExtraZipEntry.of(entryName, entryContent)).build(), null));

    assertTrue("Not initialized", initialized.get());

    Properties properties = new Properties();
    properties.load(zip.get(CONFIGURATION_PROPERTIES));
    assertEquals("Value", value, properties.getProperty(key));

    assertEquals("Version", version,
        YamlMap.from(zip.get(CONFIGURATION_FILE_NAME)).get("version").toString());

    assertTrue("Missing extra content", zip.containsKey(entryName));
    assertEquals("Extra content", entryContent,
        IOUtils.toString(zip.get(entryName), StandardCharsets.UTF_8));
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
      assertTrue("Failed to create directory: " + includeFile.getParent(), includeFile.getParentFile().mkdirs());
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
