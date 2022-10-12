/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.core.YamlSequence;

@Disabled
class WhenInliningExternalResources extends TestCase {

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
  // private ResourceResolver resourceResolver = ResourceResolver.none();

  @Test
  void shouldInlineResources() {
    String expected = someName();
    String resource = someTextFileName();
    // resourceResolver = resolveResource(resource, expected);
    String singularType = someType();
    String pluralType = English.plural(someType());
    yaml.put(singularType, Collections.singletonList(externalContentTo(resource)));
    yaml.put(pluralType, externalContentTo(resource));
    String multipleContent = English.plural(someName());
    yaml.put(multipleContent, Collections.singletonList(new YamlMap().put(CONTENT, Collections.singletonList(externalResourceTo(resource)))));

    // normalizeYaml();

    assertContentIsInlined(expected, yaml.get(singularType, 0), "list");
    assertContentIsInlined(expected, yaml.get(pluralType), "map");
    assertValue(expected, yaml.get(multipleContent, 0, CONTENT, 0, TEXT),
        String.format("Multiple content objects not inlinedinlined%n%s", yaml));
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

//  private ResourceResolver resolveResource(String supported, String resolution) {
//    return name -> {
//      if (name.equals(supported)) {
//        return resolution;
//      }
//      throw new UnknownResourceException(name, null);
//    };
//  }

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

//  private void normalizeYaml() {
//    normalizeYaml(yaml);
//  }
//
//  private YamlConfiguration normalizeYaml(YamlMap map) {
//    return new YamlConfiguration(map, resourceResolver);
//  }

  private void assertContentIsInlined(String expected, Value owner, String type) {
    assertValue(expected, owner.toMap().get(CONTENT, TEXT),
        String.format("Content in %s not inlined:%n%s", type, yaml));
  }

  @Test
  void shouldInlineNormalizedCustomPresentationHtmlTemplate() {
    String expected = someName();
    String resource = someHtmlFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put("customPresentationConfigurations", Collections.singletonList(new YamlMap()
        .put(NAME, someName())
        .put(HTML_TEMPLATE, new YamlMap()
            .put(RESOURCE, resource))));

    // normalizeYaml();

    assertCustomPresentationHasInlinedHtmlTemplate(expected);
  }

  private void assertCustomPresentationHasInlinedHtmlTemplate(String expected) {
    assertEquals(expected, yaml
        .get(English.plural("customPresentationConfiguration"), 0, HTML_TEMPLATE, TEXT).toString(),
        "Inlined resource");
  }

  @Test
  void shouldInlineSingleCustomPresentationHtmlTemplate() {
    String expected = someName();
    String resource = someHtmlFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put("customPresentationConfiguration", new YamlMap()
        .put(NAME, someName())
        .put(HTML_TEMPLATE, new YamlMap()
            .put(RESOURCE, resource)));

    // normalizeYaml();

    assertCustomPresentationHasInlinedHtmlTemplate(expected);
  }

  @Test
  void shouldInlineNamedCustomPresentationHtmlTemplate() {
    String expected = someName();
    String resource = someHtmlFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put("customPresentationConfigurations", new YamlMap()
        .put(someName(), new YamlMap()
            .put(HTML_TEMPLATE, new YamlMap()
                .put(RESOURCE, resource))));

    // normalizeYaml();

    assertCustomPresentationHasInlinedHtmlTemplate(expected);
  }

  private String someHtmlFileName() {
    return someFileName("html");
  }

  @Test
  void shouldInlineNormalizedDatabaseMetadata() {
    String expected = someName();
    String resource = someXmlFile();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(DATABASES, Collections.singletonList(new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Collections.singletonList(new YamlMap()
            .put(RESOURCE, resource)))));

    // normalizeYaml();

    assertDatabaseMetadataIsInlined(expected);
  }

  private void assertDatabaseMetadataIsInlined(String expected) {
    YamlMap databaseMetadata = yaml.get(DATABASES, 0, METADATA, 0).toMap();
    assertEquals(expected, databaseMetadata.get(TEXT).toString(), "Metadata");
  }

  @Test
  void shouldInlineSingleDatabaseMetadata() {
    String expected = someName();
    String resource = someXmlFile();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put("database", new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Collections.singletonList(new YamlMap()
            .put(RESOURCE, resource))));

    // normalizeYaml();

    assertDatabaseMetadataIsInlined(expected);
  }

  @Test
  void shouldInlineNamedDatabaseMetadata() {
    String expected = someName();
    String resource = someXmlFile();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(DATABASES, new YamlMap()
        .put(someName(), new YamlMap()
            .put(METADATA, Collections.singletonList(new YamlMap()
                .put(RESOURCE, resource)))));

    // normalizeYaml();

    assertDatabaseMetadataIsInlined(expected);
  }

  private String someXmlFile() {
    return someFileName(XML);
  }

  @Test
  void shouldInlineNormalizedTransformationXQuery() {
    String expected = someName();
    String resource = someTextFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(TRANSFORMATIONS, Collections.singletonList(new YamlMap()
        .put(NAME, someName())
        .put(XQUERY, new YamlMap()
            .put(RESOURCE, resource))));

    // normalizeYaml();

    assertTransformationXQueryIsInlined(expected);
  }

  private void assertTransformationXQueryIsInlined(String expected) {
    assertEquals(expected, yaml.get(TRANSFORMATIONS, 0, XQUERY, TEXT).toString(),
        "Inlined transformation xquery");
  }

  @Test
  void shouldInlineSingleTransformationXQuery() {
    String expected = someName();
    String resource = someTextFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(TRANSFORMATION, new YamlMap()
        .put(NAME, someName())
        .put(XQUERY, new YamlMap()
            .put(RESOURCE, resource)));

    // normalizeYaml();

    assertTransformationXQueryIsInlined(expected);
  }

  @Test
  void shouldInlineNamedTransformationXQuery() {
    String expected = someName();
    String resource = someTextFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(TRANSFORMATIONS, new YamlMap()
        .put(someName(), new YamlMap()
            .put(XQUERY, new YamlMap()
                .put(RESOURCE, resource))));

    // normalizeYaml();

    assertTransformationXQueryIsInlined(expected);
  }

  @Test
  void shouldInlineFileResourcesByPattern() {
    // resourceResolver = ResourceResolver.fromFile(new File("src/test/resources/nested-includes/root.yml"));
    yaml.put(DATABASES, Collections.singletonList(new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Collections.singletonList(new YamlMap()
            .put(RESOURCE, "**/*.yml")))));

    // normalizeYaml(yaml);

    YamlSequence contents = yaml.get(DATABASES, 0, METADATA).toList();
    assertTrue(contents.size() > 1, "# inlined:\n" + yaml);
  }

  @Test
  void shouldInlineFileResourcesByPatterns() {
    // resourceResolver = ResourceResolver.fromFile(new File("src/test/resources/configuration.properties"));
    yaml.put(DATABASES, Collections.singletonList(new YamlMap()
        .put(NAME, someName())
        .put(METADATA, Collections.singletonList(new YamlMap()
            .put(RESOURCE, Arrays.asList("nested-includes/*.yml", "*.properties"))))));

    // normalizeYaml(yaml);

    YamlSequence contents = yaml.get(DATABASES, 0, METADATA).toList();
    assertTrue(contents.size() > 1, "# inlined:\n" + yaml);
  }

  @Test
  void shouldNotInlineBinaryResources() {
    String binaryExtension = "zip";
    String resourceName = someFileName(binaryExtension);
    yaml.put(ACCESS_NODE, new YamlMap()
        .put(NAME, someName())
        .put(CONTENT, new YamlMap()
            .put(FORMAT, binaryExtension)
            .put(RESOURCE, resourceName)));

    // normalizeYaml();

    YamlMap content = yaml.get(ACCESS_NODES, 0, CONTENT).toMap();
    assertValue(resourceName, content.get(RESOURCE), "Original value should remain");
    assertTrue(content.get(TEXT).isEmpty(), "No text should be added");
  }

  @Test
  void shouldInlineNormalizedXQueryQuery() {
    String expected = someName();
    String resource = someTextFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(XQUERIES, Collections.singletonList(new YamlMap()
        .put(NAME, someName())
        .put(QUERY, new YamlMap()
            .put(RESOURCE, resource))));

    // normalizeYaml();

    assertXQueryQueryIsInlined(expected);
  }

  private void assertXQueryQueryIsInlined(String expected) {
    assertEquals(expected, yaml.get(XQUERIES, 0, QUERY).toString(),
        "Inlined transformation xquery");
  }

  @Test
  void shouldInlineSingleXQueryQuery() {
    String expected = someName();
    String resource = someTextFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(XQUERY, new YamlMap()
        .put(NAME, someName())
        .put(QUERY, new YamlMap()
            .put(RESOURCE, resource)));

    // normalizeYaml();

    assertXQueryQueryIsInlined(expected);
  }

  @Test
  void shouldInlineNamedXQueryQuery() {
    String expected = someName();
    String resource = someTextFileName();
    // resourceResolver = resolveResource(resource, expected);
    yaml.put(XQUERIES, new YamlMap()
        .put(someName(), new YamlMap()
            .put(QUERY, new YamlMap()
                .put(RESOURCE, resource))));

    // normalizeYaml();

    assertXQueryQueryIsInlined(expected);
  }

}
