/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.atteo.evo.inflector.English;
import org.junit.Test;

import com.opentext.ia.sdk.test.TestCase;
import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.YamlMap;
import com.opentext.ia.sdk.yaml.resource.ResourceResolver;


public class WhenUsingYamlConfiguration extends TestCase {

  private static final String NAME = "name";
  private static final String DEFAULT = "default";
  private static final String CONTENT = "content";
  private static final String RESOURCE = "resource";
  private static final String TEXT = "text";
  private static final String TYPE = "type";
  private static final String TENANTS = "tenants";
  private static final String TENANT = "tenant";
  private static final String APPLICATIONS = "applications";
  private static final String SPACES = "spaces";
  private static final String HOLDINGS = "holdings";
  private static final String CONFIRMATIONS = "confirmations";
  private static final String NAMESPACES = "namespaces";
  private static final String PREFIX = "prefix";
  private static final String URI = "uri";
  private static final String QUERIES = "queries";
  private static final String XDB_PDI_CONFIGS = "xdbPdiConfigs";
  private static final String OPERANDS = "operands";
  private static final String PDIS = "pdis";
  private static final String DATA = "data";
  private static final String INDEXES = "indexes";
  private static final String PATH_VALUE_INDEX = "path.value.index";
  private static final String PATH = "path";

  private final YamlMap yaml = new YamlMap();
  private ResourceResolver resourceResolver = ResourceResolver.none();

  @Test
  public void shouldInlineResources() throws Exception {
    String expected = someName();
    resourceResolver = name -> expected;
    String singularType = someType();
    String pluralType = English.plural(someType());
    String resource = someName() + ".txt";
    yaml.put(singularType, Arrays.asList(externalContentTo(resource)));
    yaml.put(pluralType, externalContentTo(resource));
    String nonContent = English.plural(someName());
    yaml.put(nonContent, Arrays.asList(new YamlMap().put(CONTENT, Arrays.asList(externalResourceTo(resource)))));

    normalizeYaml();

    assertContentIsInlined("list", expected, yaml.get(singularType, 0));
    assertContentIsInlined("map", expected, yaml.get(pluralType));
    assertValue("Invalid content structure inlined\n" + yaml, resource, yaml.get(nonContent, 0, CONTENT, 0, RESOURCE));
  }

  private String someName() {
    return randomString(5);
  }

  private String someType() {
    return randomString(8);
  }

  private YamlMap externalResourceTo(String resource) {
    return new YamlMap().put(RESOURCE, resource);
  }

  private YamlMap externalContentTo(String resource) {
    return new YamlMap()
        .put(NAME, someName())
        .put(CONTENT, externalResourceTo(resource));
  }

  private void assertContentIsInlined(String type, String expected, Value owner) {
    assertValue("Content in " + type + " not inlined:\n" + yaml, expected,
        owner.toMap().get(CONTENT, TEXT));
  }

  private void normalizeYaml() {
    new YamlConfiguration(yaml, resourceResolver);
  }

  private void assertValue(String message, String expected, Value actual) {
    assertEquals(message, expected, actual.toString());
  }

  @Test
  public void shouldAddDefaultVersionWhenNotSpecified() throws Exception {
    normalizeYaml();

    assertValue("Default version", "1.0.0", yaml.get("version"));
  }

  @Test
  public void shouldNotOverwriteSpecifiedVersion() throws Exception {
    YamlConfiguration configuration = new YamlConfiguration("version: 2.0.0");

    assertValue("Version", "2.0.0", configuration.getMap().get("version"));
  }

  @Test
  public void shouldReplaceSingularTopLevelObjectWithSequence() throws IOException {
    String name = someName();
    String type = "application";
    String otherType = someType();
    String value = someName();
    yaml.put(type, new YamlMap().put(NAME, name))
        .put(otherType, Arrays.asList(value));

    normalizeYaml();

    assertValue("Name", name, yaml.get(English.plural(type), 0, NAME));
    assertValue("Should not be changed", value, yaml.get(otherType, 0));
  }

  @Test
  public void shouldReplaceTopLevelMapOfMapsWithSequence() {
    String name = someName();
    yaml.put(APPLICATIONS, new YamlMap().put(name, new YamlMap().put(TYPE, "ACTIVE_ARCHIVING")));

    normalizeYaml();

    assertValue("Application", name, yaml.get(APPLICATIONS, 0, "name"));
  }

  @Test
  public void shouldConvertEnumValue() {
    yaml.put(APPLICATIONS, Arrays.asList(new YamlMap().put(TYPE, "active archiving")));
    yaml.put(CONFIRMATIONS, Arrays.asList(new YamlMap().put("types", Arrays.asList("receipt", "invalid"))));

    normalizeYaml();

    assertValue(TYPE, "ACTIVE_ARCHIVING", yaml.get(APPLICATIONS, 0, TYPE));
    assertValue("Types", "RECEIPT", yaml.get(CONFIRMATIONS, 0, "types", 0));
  }

  @Test
  public void shouldInsertDefaultReferences() {
    String tenant = someName();
    String application = someName();
    String space = someName();
    yaml.put(TENANTS, Arrays.asList(new YamlMap().put(NAME, tenant)));
    yaml.put(APPLICATIONS, Arrays.asList(
        new YamlMap().put(NAME, someName()),
        new YamlMap().put(NAME, application)
            .put(DEFAULT, true)));
    yaml.put(SPACES, Arrays.asList(new YamlMap().put(NAME, space)));

    normalizeYaml();

    assertValue("Tenant", tenant, yaml.get(APPLICATIONS, 0, TENANT));
    assertValue("Application", application, yaml.get(SPACES, 0, "application"));
  }

  @Test
  public void shouldNotInsertDefaultForExplicitNull() {
    yaml.put(TENANTS, Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put(APPLICATIONS, Arrays.asList(new YamlMap()
        .put(NAME, someName())
        .put(TENANT, null)));

    normalizeYaml();

    assertTrue("Explicit null is overridden with default", yaml.get(APPLICATIONS, 0, TENANT).isEmpty());
  }

  @Test
  public void shouldInsertDefaultValues() {
    yaml.put("appExportPipelines", Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put(HOLDINGS, Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("ingests", Arrays.asList(new YamlMap().put(NAME, someName())));
    yaml.put("receiverNodes", Arrays.asList(new YamlMap().put(NAME, someName())));

    normalizeYaml();

    assertTrue("appExportPipeline.includesContent", yaml.get("appExportPipelines", 0, "includesContent").toBoolean());
    assertValue("holding.xdbMode", "PRIVATE", yaml.get(HOLDINGS, 0, "xdbMode"));
    assertValue("ingest.processors.format", "xml", yaml.get("ingests", 0, "content", "format"));
    assertTrue("ingest.processors.xml", yaml.get("ingests", 0, "content", TEXT).toString().contains("sip.download"));
    assertValue("receiverNode.sips.format", "sip_zip", yaml.get("receiverNodes", 0, "sips", 0, "format"));
  }

  @Test
  public void shouldReplaceSingularObjectReferenceWithSequenceForReferenceCollections() throws IOException {
    String name = someName();
    yaml.put(HOLDINGS, Arrays.asList(new YamlMap().put(NAME, name)))
        .put(CONFIRMATIONS, Arrays.asList(new YamlMap().put("holding", name)));

    normalizeYaml();

    assertValue("Sequence of references not created", name, yaml.get(CONFIRMATIONS, 0, HOLDINGS, 0));
    assertFalse("Singular reference not removed", yaml.get(CONFIRMATIONS, 0).toMap().containsKey("holding"));
  }

  @Test
  public void shouldReplaceNestedMapOfMapsWithSequence() throws Exception {
    String query = someName();
    String operand = someName();
    String path = someName();
    yaml.put(QUERIES, Arrays.asList(new YamlMap()
        .put(NAME, query)
        .put(XDB_PDI_CONFIGS, new YamlMap()
            .put(OPERANDS, new YamlMap()
                .put(operand, new YamlMap()
                    .put(PATH, path))))));

    normalizeYaml();

    assertValue("Name", query, yaml.get(QUERIES, 0, NAME));
    assertValue("Operand", operand, yaml.get(QUERIES, 0, XDB_PDI_CONFIGS, OPERANDS, 0, NAME));
    assertValue("Path", path, yaml.get(QUERIES, 0, XDB_PDI_CONFIGS, OPERANDS, 0, PATH));
  }

  @Test
  public void shouldAddNamespaceDeclarationsToXquery() throws Exception {
    String prefix = "n";
    String uri = someUri();
    String text = "current-dateTime()";
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix)
            .put(URI, uri)))
        .put("xdbLibraryPolicies", Arrays.asList(new YamlMap()
            .put("closeHintDateQuery", new YamlMap()
                .put(TEXT, text))));

    normalizeYaml();

    assertValue("Query", String.format("declare namespace %s = \"%s\";%n%s", prefix, uri, text),
        yaml.get("xdbLibraryPolicies", 0, "closeHintDateQuery"));
  }

  private String someUri() {
    return String.format("http://%s.com/%s", someName(), someName());
  }

  @Test
  public void shouldReplacePdiSchemaNamespaceWithName() throws Exception {
    String prefix = "n";
    String uri = someUri();
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix)
            .put(URI, uri)))
        .put("pdiSchemas", Arrays.asList(new YamlMap()));

    normalizeYaml();

    assertValue("Name\n" + yaml, uri, yaml.get("pdiSchemas", 0, NAME));
  }

  @Test
  public void shouldTranslatePdiYamlToXml() {
    String prefix1 = "n";
    String uri1 = someUri();
    String prefix2 = "ex";
    String uri2 = someUri();
    yaml.put(NAMESPACES, Arrays.asList(new YamlMap()
            .put(PREFIX, prefix1)
            .put(URI, uri1),
        new YamlMap()
            .put(PREFIX, prefix2)
            .put(URI, uri2)))
        .put(PDIS, Arrays.asList(new YamlMap()
            .put(NAME, someName())
            .put(CONTENT, new YamlMap()
                .put("format", "yaml")
                .put(DATA, Arrays.asList(new YamlMap()
                    .put("id", "pdi.index.creator")
                    .put("key.document.name", "xdb.pdi.name")
                    .put(INDEXES, Arrays.asList(new YamlMap()
                        .put(someName(), new YamlMap()
                            .put(TYPE, PATH_VALUE_INDEX)
                            .put(PATH, "/n:gnu/n:gnat")), new YamlMap()
                        .put(someName(), new YamlMap()
                            .put(TYPE, PATH_VALUE_INDEX)
                            .put(PATH, "/n:foo/n:bar[n:baz]")), new YamlMap()
                        .put(someName(), new YamlMap()
                            .put(TYPE, "full.text.index")))),
                new YamlMap()
                    .put("id", "pdi.transformer")
                    .put("result.schema", prefix2)
                    .put("level", 2))))));

    normalizeYaml();

    String xml = yaml.get(PDIS, 0, CONTENT, TEXT).toString();
    assertTrue("path #1", xml.contains(String.format("/{%1$s}:gnu/{%1$s}:gnat", uri1)));
    assertTrue("path #2", xml.contains(String.format("/{%1$s}:foo/{%1$s}:bar[{%1$s}:baz]", uri1)));
    assertTrue("Default compressed", xml.contains("<compressed>false</compressed>"));
    assertTrue("Default filter.english.stop.words", xml.contains("<filter.english.stop.words>false</filter.english.stop.words>"));
    assertTrue("Schema", xml.contains(String.format("<result.schema>%s</result.schema>", uri2)));
  }

}
