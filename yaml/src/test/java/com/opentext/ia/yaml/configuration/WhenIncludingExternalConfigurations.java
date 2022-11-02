/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;

@Disabled
class WhenIncludingExternalConfigurations extends TestCase {

  private static final String NAME = "name";
  private static final String CONFIGURE = "configure";
  private static final String INCLUDES = "includes";
  private static final String VERSION = "version";
  private static final String VERSION_1 = "1.0.0";
  private static final String CONTENT = "content";
  private static final String RESOURCE = "resource";
  private static final String TEXT = "text";
  private static final String APPLICATION = "application";
  private static final String APPLICATIONS = English.plural(APPLICATION);
  private static final String FOO = "foo";
  private static final String BAR = "bar";
  private static final String BAZ = "baz";

  private final YamlMap yaml = new YamlMap();
  private ResourceResolver resourceResolver = ResourceResolver.none();

  @Test
  void shouldIncludeConfiguration() {
    String key1 = English.plural(someName());
    String key2 = someName();
    String value = someName();
    String included = new YamlMap()
        .put(key1, Collections.singletonList(new YamlMap().put(key2, value)))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(INCLUDES, Collections.singletonList(include));

    // normalizeYaml();

    assertValue(value, yaml.get(key1, 0, key2), "Included value");
    assertTrue(yaml.get(INCLUDES).isEmpty(), "Includes should be removed");
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

//  private void normalizeYaml() {
//    normalizeYaml(yaml);
//  }
//
//  private YamlConfiguration normalizeYaml(YamlMap map) {
//    return new YamlConfiguration(map, resourceResolver);
//  }

  private String someYamlFileName() {
    return someFileName("yml");
  }

  @Test
  void shouldIgnoreDuplicateEntryConfiguredAsExisting() {
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
    yaml.put(collection, Collections.singletonList(new YamlMap().put(NAME, name)))
        .put(INCLUDES, Collections.singletonList(include));

    // normalizeYaml();

    assertValue(name, yaml.get(collection, 0, NAME), NAME);
    assertFalse(yaml.containsKey(type), "Singular should not be added");
  }

  @Test
  void shouldIgnoreDuplicateEntriesConfiguredAsExisting() {
    String type = someName();
    String collection = English.plural(type);
    String name = someName();
    String included = new YamlMap()
        .put(collection, Collections.singletonList(new YamlMap()
                .put(NAME, name)
                .put(CONFIGURE, ObjectConfiguration.USE_EXISTING.toString())))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(collection, Collections.singletonList(new YamlMap().put(NAME, name)))
        .put(INCLUDES, Collections.singletonList(include));

    // normalizeYaml();

    Value object = yaml.get(collection, 0);
    assertValue(name, object.toMap().get(NAME), NAME);
    assertValue("", yaml.get(CONFIGURE), CONFIGURE);
  }

  @Test
  void shouldReplaceOriginalEntryConfiguredAsExisting() {
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
        .put(collection, Collections.singletonList(new YamlMap().put(NAME, name).put(property, value)))
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

    // normalizeYaml();

    assertTrue(yaml.get(type).isEmpty(), "Singular should not be added");
    YamlMap map = yaml.get(collection, 0).toMap();
    assertValue(name, map.get(NAME), NAME);
    assertValue(value, map.get(property), "Other property");
  }

  @Test
  void shouldIgnoreDuplicateVersionEntry() {
    String included = new YamlMap()
        .put(VERSION, VERSION_1)
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(VERSION, VERSION_1)
        .put(INCLUDES, Collections.singletonList(include));

    // normalizeYaml();

    assertValue(VERSION_1, yaml.get(VERSION), VERSION);
  }

  @Test
  void shouldIgnoreDuplicateNamespaceEntry() {
    YamlMap namespace1 = someNamespace();
    YamlMap namespace2 = someNamespace();
    String included = new YamlMap()
        .put("namespace", namespace1)
        .put("namespaces", Collections.singletonList(namespace2))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put("namespace", someName())
        .put("namespaces", Collections.singletonList(namespace2))
        .put(INCLUDES, Collections.singletonList(include));

    // normalizeYaml();

    // Should not throw exception
  }

  private YamlMap someNamespace() {
    return new YamlMap().put("uri", someUri()).put("prefix", someName());
  }

  private String someUri() {
    return String.format("http://%s.com/%s", someName(), someName());
  }

  @Test
  void shouldResolveInlineResourcesRelativeToIncludedResource() {
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
    yaml.put(INCLUDES, Collections.singletonList(include));

    // normalizeYaml();

    assertValue(text, yaml.get(English.plural(key), 0, CONTENT, TEXT), "Included resource should be resolved");
  }

  private String someHtmlFileName() {
    return someFileName("html");
  }

  @Test
  void shouldSubstitutePropertiesInInlinedYaml() {
    String collection = English.plural(someName());
    resourceResolver = ResourceResolver.fromClasspath();
    yaml.put(collection, Collections.singletonList(new YamlMap().put(NAME, "${qux}")))
        .put(INCLUDES, Collections.singletonList("include/configuration.yml"));

    // normalizeYaml();

    assertValue("thud", yaml.get(collection, 0, NAME), "Substituted value");
    assertValue("bar", yaml.get("inc", 0, "foo"), "Inherited inline value");
    assertValue("xyzzy", yaml.get("inc", 0, NAME), "Overridden inline value");
  }

  @Test
  void shouldInlineBasedOnSubstitutedProperty() {
    resourceResolver = ResourceResolver.fromClasspath("/stores");
    YamlMap map = YamlMap.from(resourceResolver.apply("configuration.yml"));

    // normalizeYaml(map);

    assertValue("aws-s3", map.get("stores", 0, NAME), "Store name");
  }

  @Test
  void shouldInlineNestedIncludes() {
    resourceResolver = ResourceResolver.fromClasspath("/nested-includes");
    yaml.put(INCLUDES, Collections.singletonList("root.yml"));

    // normalizeYaml(yaml);

    assertValue("bar", yaml.get("foo"), "Inlined value");
  }

  @Test
  void shouldNotInlineWhenIncludeIsConfiguredToBeIgnored() {
    String key1 = English.plural(someName());
    String key2 = someName();
    String value = someName();
    String included = new YamlMap()
        .put(key1, Collections.singletonList(new YamlMap().put(key2, value)))
        .toString();
    String include = someYamlFileName();
    resourceResolver = resolveResource(include, included);
    yaml.put(INCLUDES, Collections.singletonList(new YamlMap()
        .put(RESOURCE, include)
        .put(CONFIGURE, ObjectConfiguration.IGNORE.toString())));

    // normalizeYaml();

    assertFalse(yaml.containsKey(key1), "Value should not be included");
  }

  @Test
  void shouldMergedInlinedYaml() {
    resourceResolver = name -> {
      switch (name) {
        case FOO:
          return new YamlMap()
              .put(APPLICATION, new YamlMap()
                  .put(NAME, FOO))
              .toString();
        case BAR:
          return new YamlMap()
              .put(APPLICATION, new YamlMap()
                  .put(NAME, BAR))
              .toString();
        case BAZ:
          return new YamlMap()
              .put(APPLICATIONS, Collections.singletonList(new YamlMap()
                  .put(NAME, BAZ)))
              .toString();
        default:
          throw new UnknownResourceException(name, null);
      }
    };
    yaml.put("includes", Arrays.asList(FOO, BAR, BAZ));

    // normalizeYaml();

    YamlMap inlined = yaml.sort();
    assertValue(BAR, inlined.get(APPLICATIONS, 0, NAME), BAR);
    assertValue(BAZ, inlined.get(APPLICATIONS, 1, NAME), BAZ);
    assertValue(FOO, inlined.get(APPLICATIONS, 2, NAME), FOO);
  }

}
