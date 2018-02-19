/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.atteo.evo.inflector.English;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


public class WhenIncludingExternalConfigurations extends TestCase {

  private static final String NAME = "name";
  private static final String CONFIGURE = "configure";
  private static final String INCLUDES = "includes";
  private static final String VERSION = "version";
  private static final String VERSION_1 = "1.0.0";
  private static final String CONTENT = "content";
  private static final String RESOURCE = "resource";
  private static final String TEXT = "text";

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private final YamlMap yaml = new YamlMap();
  private ResourceResolver resourceResolver = ResourceResolver.none();

  @Test
  public void shouldIncludeConfiguration() {
    String key1 = English.plural(someName());
    String key2 = someName();
    String value = someName();
    String included = new YamlMap()
        .put(key1, Arrays.asList(new YamlMap().put(key2, value)))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(INCLUDES, Arrays.asList(include));

    normalizeYaml();

    assertValue("Included value", value, yaml.get(key1, 0, key2));
    assertTrue("Includes should be removed", yaml.get(INCLUDES).isEmpty());
  }

  private String someName() {
    return randomString(5);
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

  private void normalizeYaml() {
    normalizeYaml(yaml);
  }

  private YamlConfiguration normalizeYaml(YamlMap map) {
    return new YamlConfiguration(map, resourceResolver);
  }

  private String someYamlFileName() {
    return someFileName("yml");
  }

  private void assertValue(String message, String expected, Value actual) {
    assertEquals(message, expected, actual.toString());
  }

  @Test
  public void shouldFailToIncludeDuplicateEntry() throws Exception {
    String key = English.plural(someName());
    String included = new YamlMap()
        .put(key, someName())
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(key, someName())
        .put(INCLUDES, Arrays.asList(include));

    thrown.expect(IllegalArgumentException.class);
    normalizeYaml();
  }

  @Test
  public void shouldIgnoreDuplicateEntryConfiguredAsExisting() throws Exception {
    String type = someName();
    String collection = English.plural(type);
    String name = someName();
    String included = new YamlMap()
        .put(type, new YamlMap()
                .put(NAME, name)
                .put(CONFIGURE, ObjectConfiguration.USE_EXISTING.toString()))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(collection, Arrays.asList(new YamlMap().put(NAME, name)))
        .put(INCLUDES, Arrays.asList(include));

    normalizeYaml();

    assertValue(NAME, name, yaml.get(collection, 0, NAME));
    assertFalse("Singular should not be added", yaml.containsKey(type));
  }

  @Test
  public void shouldIgnoreDuplicateEntriesConfiguredAsExisting() throws Exception {
    String type = someName();
    String collection = English.plural(type);
    String name = someName();
    String included = new YamlMap()
        .put(collection, Arrays.asList(new YamlMap()
                .put(NAME, name)
                .put(CONFIGURE, ObjectConfiguration.USE_EXISTING.toString())))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(collection, Arrays.asList(new YamlMap().put(NAME, name)))
        .put(INCLUDES, Arrays.asList(include));

    normalizeYaml();

    Value object = yaml.get(collection, 0);
    assertValue(NAME, name, object.toMap().get(NAME));
    assertValue(CONFIGURE, "", yaml.get(CONFIGURE));
  }

  @Test
  public void shouldReplaceOriginalEntryConfiguredAsExisting() throws Exception {
    String type = someName();
    String collection = English.plural(type);
    String name = someName();
    String property = someName();
    String value = someName();
    String included1 = new YamlMap()
        .put(type, new YamlMap().put(NAME, name).put(CONFIGURE, ObjectConfiguration.USE_EXISTING.toString()))
        .toString();
    String include1 = someYamlFileName();
    String included2 = new YamlMap()
        .put(collection, Arrays.asList(new YamlMap().put(NAME, name).put(property, value)))
        .toString();
    String include2 = someYamlFileName();
    resourceResolver = resource -> {
      if (resource.equals(include1)) {
        return included1;
      }
      if (resource.equals(include2)) {
        return included2;
      }
      throw new UnknownResourceException(resource, null);
    };
    yaml.put(INCLUDES, Arrays.asList(include1, include2));

    normalizeYaml();

    assertTrue("Singular should not be added", yaml.get(type).isEmpty());
    YamlMap map = yaml.get(collection, 0).toMap();
    assertValue(NAME, name, map.get(NAME));
    assertValue("Other property", value, map.get(property));
  }

  @Test
  public void shouldIgnoreDuplicateVersionEntry() throws Exception {
    String included = new YamlMap()
        .put(VERSION, VERSION_1)
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(VERSION, VERSION_1)
        .put(INCLUDES, Arrays.asList(include));

    normalizeYaml();

    assertValue(VERSION, VERSION_1, yaml.get(VERSION));
  }

  @Test
  public void shouldIgnoreDuplicateNamespaceEntry() throws Exception {
    YamlMap namespace1 = someNamespace();
    YamlMap namespace2 = someNamespace();
    String included = new YamlMap()
        .put("namespace", namespace1)
        .put("namespaces", Arrays.asList(namespace2))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put("namespace", someName())
        .put("namespaces", Arrays.asList(namespace2))
        .put(INCLUDES, Arrays.asList(include));

    normalizeYaml();

    // Should not throw exception
  }

  private YamlMap someNamespace() {
    return new YamlMap().put("uri", someUri()).put("prefix", someName());
  }

  private String someUri() {
    return String.format("http://%s.com/%s", someName(), someName());
  }

  @Test
  public void shouldResolveInlineResourcesRelativeToIncludedResource() throws Exception {
    String dir = someName();
    String file = someHtmlFileName();
    String relativeFile = dir + '/' + file;
    String key = someName() + 'n';
    String included = new YamlMap()
        .put(key, new YamlMap()
            .put(NAME, someName())
            .put(CONTENT, new YamlMap()
                .put(RESOURCE, file)))
        .toString();
    String include = dir + '/' + someYamlFileName();
    String text = randomString(64);
    resourceResolver = name -> {
      if (name.equals(include)) {
        return included;
      }
      if (name.equals(relativeFile)) {
        return text;
      }
      throw new UnknownResourceException(name, null);
    };
    yaml.put(INCLUDES, Arrays.asList(include));

    normalizeYaml();

    assertValue("Included resource should be resolved", text, yaml.get(English.plural(key), 0, CONTENT, TEXT));
  }

  private String someHtmlFileName() {
    return someFileName("html");
  }

  @Test
  public void shouldSubstitutePropertiesInInlinedYaml() {
    String collection = English.plural(someName());
    resourceResolver = ResourceResolver.fromClasspath();
    yaml.put(collection, Arrays.asList(new YamlMap().put(NAME, "${qux}")))
        .put(INCLUDES, Arrays.asList("include/configuration.yml"));

    normalizeYaml();

    assertValue("Substituted value", "thud", yaml.get(collection, 0, NAME));
    assertValue("Inherited inline value", "bar", yaml.get("inc", 0, "foo"));
    assertValue("Overridden inline value", "xyzzy", yaml.get("inc", 0, NAME));
  }

  @Test
  public void shouldInlineBasedOnSubstitutedProperty() {
    resourceResolver = ResourceResolver.fromClasspath("/stores");
    YamlMap map = YamlMap.from(resourceResolver.apply("configuration.yml"));

    normalizeYaml(map);

    assertValue("Store name", "aws-s3", map.get("stores", 0, NAME));
  }

  @Test
  public void shouldInlineNestedIncludes() {
    resourceResolver = ResourceResolver.fromClasspath("/nested-includes");
    yaml.put(INCLUDES, Arrays.asList("root.yml"));

    normalizeYaml(yaml);

    assertValue("Inlined value", "bar", yaml.get("foo"));
  }

  @Test
  public void shouldNotInlineWhenIncludeIsConfiguredToBeIgnored() {
    String key1 = English.plural(someName());
    String key2 = someName();
    String value = someName();
    String included = new YamlMap()
        .put(key1, Arrays.asList(new YamlMap().put(key2, value)))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(INCLUDES, Arrays.asList(new YamlMap()
        .put(RESOURCE, include)
        .put(CONFIGURE, ObjectConfiguration.IGNORE.toString())));

    normalizeYaml();

    assertFalse("Value should not be included", yaml.containsKey(key1));
  }

}
