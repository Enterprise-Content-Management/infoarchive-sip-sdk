/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.atteo.evo.inflector.English;
import org.junit.Test;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.core.YamlSequence;
import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


public class WhenInliningExternalResources extends TestCase {

  private static final String NAME = "name";
  private static final String CONTENT = "content";
  private static final String RESOURCE = "resource";
  private static final String TEXT = "text";
  private static final String FORMAT = "format";
  private static final String TRANSFORMATION = "transformation";
  private static final String TRANSFORMATIONS = English.plural(TRANSFORMATION);
  private static final String XQUERY = "xquery";
  private static final String XQUERIES = English.plural(XQUERY);
  private static final String QUERY = "query";
  private static final String HTML_TEMPLATE = "htmlTemplate";
  private static final String DATABASES = "databases";
  private static final String METADATA = "metadata";
  private static final String XML = "xml";
  private static final String ACCESS_NODE = "accessNode";
  private static final String ACCESS_NODES = English.plural(ACCESS_NODE);

  private final YamlMap yaml = new YamlMap();
  private ResourceResolver resourceResolver = ResourceResolver.none();

  @Test
  public void shouldInlineResources() {
    String expected = someName();
    String resource = someTextFileName();
    resourceResolver = resolveResource(resource, expected);
    String singularType = someType();
    String pluralType = English.plural(someType());
    yaml.put(singularType, Arrays.asList(externalContentTo(resource)));
    yaml.put(pluralType, externalContentTo(resource));
    String multipleContent = English.plural(someName());
    yaml.put(multipleContent, Arrays.asList(new YamlMap().put(CONTENT, Arrays.asList(externalResourceTo(resource)))));

    normalizeYaml();

    assertContentIsInlined("list", expected, yaml.get(singularType, 0));
    assertContentIsInlined("map", expected, yaml.get(pluralType));
    assertValue(String.format("Multiple content objects not inlinedinlined%n%s", yaml), expected,
        yaml.get(multipleContent, 0, CONTENT, 0, TEXT));
  }

  private String someName() {
    return randomString(5);
  }

  private String someTextFileName() {
    return someFileName("txt");
  }

  private String someFileName(String extension) {
    return someName() + '.' + extension;
  }

  private ResourceResolver resolveResource(String supported, String resolution) {
    return name -> {
      if (name.equals(supported)) {
        return resolution;
      }
      throw new UnknownResourceException(name, null);
    };
  }

  private String someType() {
    return randomString(8);
  }

  private YamlMap externalContentTo(String resource) {
    return new YamlMap()
        .put(NAME, someName())
        .put(CONTENT, externalResourceTo(resource));
  }

  private YamlMap externalResourceTo(String resource) {
    return new YamlMap().put(RESOURCE, resource);
  }

  private void normalizeYaml() {
    normalizeYaml(yaml);
  }

  private YamlConfiguration normalizeYaml(YamlMap map) {
    return new YamlConfiguration(map, resourceResolver);
  }

  private void assertContentIsInlined(String type, String expected, Value owner) {
    assertValue(String.format("Content in %s not inlined:%n%s", type, yaml), expected,
        owner.toMap().get(CONTENT, TEXT));
  }

  private void assertValue(String message, String expected, Value actual) {
    assertEquals(message, expected, actual.toString());
  }

  @Test
  public void shouldInlineNormalizedCustomPresentationHtmlTemplate() {
    String expected = someName();
    String resource = someHtmlFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put("customPresentationConfigurations", Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(HTML_TEMPLATE, new YamlMap()
            .put(RESOURCE, resource))));

    normalizeYaml();

    assertCustomPresentationHasInlinedHtmlTemplate(expected);
  }

  private void assertCustomPresentationHasInlinedHtmlTemplate(String expected) {
    assertEquals("Inlined resource", expected,
        yaml.get(English.plural("customPresentationConfiguration"), 0, HTML_TEMPLATE, TEXT).toString());
  }

  @Test
  public void shouldInlineSingleCustomPresentationHtmlTemplate() {
    String expected = someName();
    String resource = someHtmlFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put("customPresentationConfiguration", new YamlMap()
        .put(NAME, someName())
        .put(HTML_TEMPLATE, new YamlMap()
            .put(RESOURCE, resource)));

    normalizeYaml();

    assertCustomPresentationHasInlinedHtmlTemplate(expected);
  }

  @Test
  public void shouldInlineNamedCustomPresentationHtmlTemplate() {
    String expected = someName();
    String resource = someHtmlFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put("customPresentationConfigurations", new YamlMap()
        .put(someName(), new YamlMap()
            .put(HTML_TEMPLATE, new YamlMap()
                .put(RESOURCE, resource))));

    normalizeYaml();

    assertCustomPresentationHasInlinedHtmlTemplate(expected);
  }

  private String someHtmlFileName() {
    return someFileName("html");
  }

  @Test
  public void shouldInlineNormalizedDatabaseMetadata() {
    String expected = someName();
    String resource = someXmlFile();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(DATABASES, Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Arrays.asList(new YamlMap()
            .put(RESOURCE, resource)))));

    normalizeYaml();

    assertDatabaseMetadataIsInlined(expected);
  }

  private void assertDatabaseMetadataIsInlined(String expected) {
    YamlMap databaseMetadata = yaml.get(DATABASES, 0, METADATA, 0).toMap();
    assertEquals("Metadata", expected, databaseMetadata.get(TEXT).toString());
  }

  @Test
  public void shouldInlineSingleDatabaseMetadata() {
    String expected = someName();
    String resource = someXmlFile();
    resourceResolver = resolveResource(resource, expected);
    yaml.put("database", new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Arrays.asList(new YamlMap()
            .put(RESOURCE, resource))));

    normalizeYaml();

    assertDatabaseMetadataIsInlined(expected);
  }

  @Test
  public void shouldInlineNamedDatabaseMetadata() {
    String expected = someName();
    String resource = someXmlFile();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(DATABASES, new YamlMap()
        .put(someName(), new YamlMap()
            .put(METADATA, Arrays.asList(new YamlMap()
                .put(RESOURCE, resource)))));

    normalizeYaml();

    assertDatabaseMetadataIsInlined(expected);
  }

  private String someXmlFile() {
    return someFileName(XML);
  }

  @Test
  public void shouldInlineNormalizedTransformationXQuery() {
    String expected = someName();
    String resource = someTextFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(TRANSFORMATIONS, Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(XQUERY, new YamlMap()
            .put(RESOURCE, resource))));

    normalizeYaml();

    assertTransformationXQueryIsInlined(expected);
  }

  private void assertTransformationXQueryIsInlined(String expected) {
    assertEquals("Inlined transformation xquery", expected,
        yaml.get(TRANSFORMATIONS, 0, XQUERY, TEXT).toString());
  }

  @Test
  public void shouldInlineSingleTransformationXQuery() {
    String expected = someName();
    String resource = someTextFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(TRANSFORMATION, new YamlMap()
        .put(NAME, someName())
        .put(XQUERY, new YamlMap()
            .put(RESOURCE, resource)));

    normalizeYaml();

    assertTransformationXQueryIsInlined(expected);
  }

  @Test
  public void shouldInlineNamedTransformationXQuery() {
    String expected = someName();
    String resource = someTextFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(TRANSFORMATIONS, new YamlMap()
        .put(someName(), new YamlMap()
            .put(XQUERY, new YamlMap()
                .put(RESOURCE, resource))));

    normalizeYaml();

    assertTransformationXQueryIsInlined(expected);
  }

  @Test
  public void shouldInlineFileResourcesByPattern() {
    resourceResolver = ResourceResolver.fromFile(new File("src/test/resources/nested-includes/root.yml"));
    yaml.put(DATABASES, Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Arrays.asList(new YamlMap()
            .put(RESOURCE, "**/*.yml")))));

    normalizeYaml(yaml);

    YamlSequence contents = yaml.get(DATABASES, 0, METADATA).toList();
    assertTrue("# inlined:\n" + yaml, contents.size() > 1);
  }

  @Test
  public void shouldInlineFileResourcesByPatterns() {
    resourceResolver = ResourceResolver.fromFile(new File("src/test/resources/configuration.properties"));
    yaml.put(DATABASES, Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Arrays.asList(new YamlMap()
            .put(RESOURCE, Arrays.asList("nested-includes/*.yml", "*.properties"))))));

    normalizeYaml(yaml);

    YamlSequence contents = yaml.get(DATABASES, 0, METADATA).toList();
    assertTrue("# inlined:\n" + yaml, contents.size() > 1);
  }

  @Test
  public void shouldNotInlineBinaryResources() {
    String binaryExtension = "zip";
    String resourceName = someFileName(binaryExtension);
    yaml.put(ACCESS_NODE, new YamlMap()
        .put(NAME, someName())
        .put(CONTENT, new YamlMap()
            .put(FORMAT, binaryExtension)
            .put(RESOURCE, resourceName)));

    normalizeYaml();

    YamlMap content = yaml.get(ACCESS_NODES, 0, CONTENT).toMap();
    assertValue("Original value should remain", resourceName, content.get(RESOURCE));
    assertTrue("No text should be added", content.get(TEXT).isEmpty());
  }

  @Test
  public void shouldInlineNormalizedXQueryQuery() {
    String expected = someName();
    String resource = someTextFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(XQUERIES, Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(QUERY, new YamlMap()
            .put(RESOURCE, resource))));

    normalizeYaml();

    assertXQueryQueryIsInlined(expected);
  }

  private void assertXQueryQueryIsInlined(String expected) {
    assertEquals("Inlined transformation xquery", expected,
        yaml.get(XQUERIES, 0, QUERY).toString());
  }

  @Test
  public void shouldInlineSingleXQueryQuery() {
    String expected = someName();
    String resource = someTextFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(XQUERY, new YamlMap()
        .put(NAME, someName())
        .put(QUERY, new YamlMap()
            .put(RESOURCE, resource)));

    normalizeYaml();

    assertXQueryQueryIsInlined(expected);
  }

  @Test
  public void shouldInlineNamedXQueryQuery() {
    String expected = someName();
    String resource = someTextFileName();
    resourceResolver = resolveResource(resource, expected);
    yaml.put(XQUERIES, new YamlMap()
        .put(someName(), new YamlMap()
            .put(QUERY, new YamlMap()
                .put(RESOURCE, resource))));

    normalizeYaml();

    assertXQueryQueryIsInlined(expected);
  }

}
