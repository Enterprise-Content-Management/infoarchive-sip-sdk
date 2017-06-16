/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.atteo.evo.inflector.English;
import org.junit.Test;

import com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties;
import com.opentext.ia.sdk.support.resource.ResourceResolver;
import com.opentext.ia.sdk.support.test.TestCase;
import com.opentext.ia.sdk.support.yaml.Value;
import com.opentext.ia.sdk.support.yaml.YamlMap;


public class WhenUsingYamlConfiguration extends TestCase implements InfoArchiveConfigurationProperties {

  private static final String NAME = "name";
  private static final String CONTENT = "content";
  private static final String RESOURCE = "resource";

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
    String nonContent = someName();
    yaml.put(nonContent, new YamlMap().put(CONTENT, Arrays.asList(externalResourceTo(resource))));

    normalizeYaml();

    assertContentIsInlined("list", expected, yaml.get(singularType, 0));
    assertContentIsInlined("map", expected, yaml.get(pluralType));
    assertValue("Invalid content structure inlined\n" + yaml, resource, yaml.get(nonContent, CONTENT, 0, RESOURCE));
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
    return new YamlMap().put(CONTENT, externalResourceTo(resource));
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
  public void shouldConvertEnumValue() {
    yaml.put("applications", Arrays.asList(new YamlMap().put("type", "active archiving")));
    yaml.put("confirmations", Arrays.asList(new YamlMap().put("types", Arrays.asList("receipt", "invalid"))));

    normalizeYaml();

    assertValue("Type", "ACTIVE_ARCHIVING", yaml.get("applications", 0, "type"));
    assertValue("Types", "RECEIPT", yaml.get("confirmations", 0, "types", 0));
  }

}
