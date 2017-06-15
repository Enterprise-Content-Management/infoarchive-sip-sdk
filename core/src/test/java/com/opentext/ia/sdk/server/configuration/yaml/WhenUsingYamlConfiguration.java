/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import org.atteo.evo.inflector.English;
import org.junit.Ignore;
import org.junit.Test;

import com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties;
import com.opentext.ia.sdk.support.resource.ResourceResolver;
import com.opentext.ia.sdk.support.test.TestCase;
import com.opentext.ia.sdk.support.yaml.YamlMap;


public class WhenUsingYamlConfiguration extends TestCase implements InfoArchiveConfigurationProperties {

  private final YamlMap yaml = new YamlMap();
  private final ResourceResolver resourceResolver = ResourceResolver.fromClasspath("/config");

  @Test
  @Ignore("TODO: Make this work again")
  public void shouldInlineResources() throws Exception {
    yaml.put("foos", Arrays.asList(new YamlMap().put("resource", "external.txt")));

    configure();

    String text = yaml.get("foos", 0, "text").toString();
    assertTrue("Resource not inlined:\n" + text, text.contains("foo"));
  }

  private void configure() {
    new YamlConfiguration(yaml, resourceResolver);
  }

  @Test
  public void shouldAddDefaultVersionWhenNotSpecified() throws Exception {
    configure();

    assertEquals("Default version", "1.0.0", yaml.get("version").toString());
  }

  @Test
  public void shouldNotOverwriteSpecifiedVersion() throws Exception {
    YamlConfiguration configuration = new YamlConfiguration("version: 2.0.0");

    assertEquals("Version", "2.0.0", configuration.getMap().get("version").toString());
  }

  @Test
  @Ignore("TODO: Implement")
  public void shouldReplaceSingularTopLevelObjectWithSequence() throws IOException {
    String name = someName();
    String type = someType();
    yaml.put(type, new YamlMap().put("name", name));

    configure();

    assertEquals("Name", name, yaml.get(English.plural(type), 0, "name").toString());
  }

  private String someName() {
    return randomString(5);
  }

  private String someType() {
    return randomString(8);
  }

  private void assertName(String name, String type, YamlConfiguration configuration) {
    assertEquals("Name", name, configuration.nameOfSingle(type));
  }

  @Test
  public void shouldFindPluralTopLevelObjectWithOneItemInSequence() throws IOException {
    String name = someName();
    String type = someType();
    YamlConfiguration configuration = new YamlConfiguration(String.format("%s:%n- name: %s", English.plural(type), name));

    assertName(name, type, configuration);
  }

  @Test
  public void shouldFindPluralTopLevelObjectWithItemMarkedAsDefaultInSequence() throws IOException {
    String name = someName();
    String type = someType();
    YamlConfiguration configuration = new YamlConfiguration(String.format("%s:%n- name: %s%n- name: %s%n  default: true",
        English.plural(type), someName(), name));

    assertName(name, type, configuration);
  }

  @Test
  public void shouldFindPluralTopLevelObjectWithOneItemInMap() throws IOException {
    String name = someName();
    String type = someType();
    YamlConfiguration configuration = new YamlConfiguration(String.format("%s:%n  %s: ~", English.plural(type), name));

    assertName(name, type, configuration);
  }

  @Test
  public void shouldFindPluralTopLevelObjectWithItemMarkedAsDefaultInMap() throws IOException {
    String name = someName();
    String type = someType();
    YamlConfiguration configuration = new YamlConfiguration(String.format(
        "%s:%n  %s: ~%n  %s:%n    default: false%n  %s:%n    default: true", English.plural(type), someName(), someName(),
        name));

    assertName(name, type, configuration);
  }

  @Test
  public void shouldNotFindNonExistingTopLevelObject() {
    YamlConfiguration configuration = new YamlConfiguration("foo: bar");

    assertTrue("Non-existing item found", configuration.nameOfSingle(someType()).isEmpty());
  }

}
