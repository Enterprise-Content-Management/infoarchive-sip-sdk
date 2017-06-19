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
  private static final String TENANTS = "tenants";
  private static final String TENANT = "tenant";
  private static final String APPLICATIONS = "applications";
  private static final String SPACES = "spaces";
  private static final String HOLDINGS = "holdings";
  private static final String CONFIRMATIONS = "confirmations";
  private static final String TYPE = "type";

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
        owner.toMap().get(CONTENT, "text"));
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
    assertTrue("ingest.processors.xml", yaml.get("ingests", 0, "content", "text").toString().contains("sip.download"));
    assertValue("receiverNode.sips.format", "sip_zip", yaml.get("receiverNodes", 0, "sips", 0, "format"));
  }

  @Test
  public void shouldReplaceSingularObjectReferenceWithSequence() throws IOException {
    String name = someName();
    yaml.put(HOLDINGS, Arrays.asList(new YamlMap().put(NAME, name)))
        .put(CONFIRMATIONS, Arrays.asList(new YamlMap().put("holding", name)));

    normalizeYaml();

    assertValue("Sequence of references not created", name, yaml.get(CONFIRMATIONS, 0, HOLDINGS, 0));
    assertFalse("Singular reference not removed", yaml.get(CONFIRMATIONS, 0).toMap().containsKey("holding"));
  }

}
