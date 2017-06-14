/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atteo.evo.inflector.English;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties;
import com.opentext.ia.sdk.server.configuration.yaml.YamlConfiguration;
import com.opentext.ia.sdk.support.resource.ResourceResolver;
import com.opentext.ia.sdk.support.test.TestCase;


public class WhenUsingYamlConfiguration extends TestCase implements InfoArchiveConfigurationProperties {

  private static final String EXPECTED_TENANT_NAME = "myTenant";
  private static final String EXPECTED_APPLICATION_NAME = "myApplication";
  private static final String EXPECTED_QUERY_NAME = "myQuery";
  private static final String EXPECTED_SEARCH_NAME = "mySearch";
  private static final String EXPECTED_SEARCH_COMPOSITION_NAME = "mySearchComposition";

  private String yaml;

  @Before
  public void init() {
    Map<String, Object> root = new HashMap<>();
    root.put("tenant", EXPECTED_TENANT_NAME);

    Map<String, String> application = new HashMap<>();
    application.put(NAME, EXPECTED_APPLICATION_NAME);
    root.put("application", application);

    List<Map<String, String>> queries = new ArrayList<>();
    Map<String, String> query = new HashMap<>();
    query.put(NAME, EXPECTED_QUERY_NAME);
    queries.add(query);
    root.put("query", queries);

    List<Object> searches = new ArrayList<>();
    Map<String, Object> search = new HashMap<>();
    search.put(NAME, EXPECTED_SEARCH_NAME);
    List<Map<String, String>> compositions = new ArrayList<>();
    Map<String, String> composition = new HashMap<>();
    composition.put(NAME, EXPECTED_SEARCH_COMPOSITION_NAME);
    compositions.add(composition);
    search.put("composition", compositions);
    searches.add(search);
    root.put("search", searches);

    yaml = new Yaml().dumpAsMap(root);
  }

  @Test
  public void shouldParseYamlSuccessfully() throws IOException {
    Map<String, String> config = new YamlConfiguration(yaml).toMap();

    assertEquals("Tenant name", EXPECTED_TENANT_NAME, config.get(TENANT_NAME));
    assertEquals("Application name", EXPECTED_APPLICATION_NAME, config.get(APPLICATION_NAME));
    assertEquals("Query name", EXPECTED_QUERY_NAME, config.get(QUERY_NAME));
    assertEquals("Search name", EXPECTED_SEARCH_NAME, config.get(SEARCH_NAME));
    assertEquals("Search composition name", EXPECTED_SEARCH_COMPOSITION_NAME, config.get(SEARCH_COMPOSITION_NAME));
  }

  @Test
  public void shouldInlineResources() throws Exception {
    try (InputStream configuration = getClass().getResourceAsStream("/config/configuration.yml")) {
      YamlConfiguration yamlConfiguration = new YamlConfiguration(configuration, ResourceResolver.fromClasspath("/config"));
      String text = yamlConfiguration.toString();
      assertTrue("Resource not inlined:\n" + text, text.contains("foo"));
    }
  }

  @Test
  public void shouldFindSingularTopLevelObject() throws IOException {
    String name = someName();
    String type = someType();
    YamlConfiguration configuration = new YamlConfiguration(String.format("%s:%n  name: %s", type, name));

    assertName(name, type, configuration);
  }

  private String someName() {
    return randomString(16);
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
