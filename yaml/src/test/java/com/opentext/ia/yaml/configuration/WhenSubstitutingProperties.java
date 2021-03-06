/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

import com.opentext.ia.yaml.resource.ResourceResolver;


public class WhenSubstitutingProperties {

  private final ResourceResolver resourceResolver = ResourceResolver.fromClasspath();
  private Function<String, String> propertyResolver;

  @Test
  public void shouldReplacePropertyReferenceWithValue() {
    propertyResolver = new ConfigurationProperties(resourceResolver, "configuration.properties");
    assertEquals("Non-expression", "foo", propertyResolver.apply("foo"));
    assertProperty("Use existing value", "bar", "foo");
    assertProperty("Substitute with value", "gnat", "baz");
    assertProperty("Substitute with default if not found", "quux", "qux");
    assertProperty("Substitute with empty default", "", "waldo");
    assertProperty("Don't substitute if not found and no default", "${grault}", "corge");
    assertProperty("Keep prefix and suffix", "hamANDeggs", "spam");
    assertProperty("Multiple subsitutions", "http://localhost:8765/services", "url");
    assertProperty("Multiple subsitutions", "http://localhost:8765/services", "url");
    assertPropertyExpression("Empty value should not override", "bottle", "${empty:bottle}");
  }

  private void assertProperty(String message, String expected, String name) {
    assertPropertyExpression(message, expected, String.format("${%s}", name));
  }

  private void assertPropertyExpression(String message, String expected, String expression) {
    assertEquals(message, expected, propertyResolver.apply(expression));
  }

  @Test
  public void shouldOverrideParentProperties() {
    propertyResolver = new ConfigurationProperties(resourceResolver, "configuration.properties",
        new ConfigurationProperties(resourceResolver, "1.properties",
            new ConfigurationProperties(resourceResolver, "0.properties")));

    assertProperty("From parent", "thud", "qux");
    assertProperty("From grandparent", "garply", "corge");
  }

  @Test
  public void shouldTrimValues() {
    propertyResolver = new ConfigurationProperties(resourceResolver, "configuration.properties");
    assertEquals("Trimmed value", "bar", propertyResolver.apply("${foo}"));
  }

}
