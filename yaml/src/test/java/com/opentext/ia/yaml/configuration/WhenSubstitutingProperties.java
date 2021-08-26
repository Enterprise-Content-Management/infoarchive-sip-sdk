/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.opentext.ia.yaml.resource.ResourceResolver;


public class WhenSubstitutingProperties {

  private final ResourceResolver resourceResolver = ResourceResolver.fromClasspath();
  private Function<String, String> propertyResolver;

  @Test
  public void shouldReplacePropertyReferenceWithValue() {
    propertyResolver = new ConfigurationProperties(resourceResolver, "configuration.properties");
    assertEquals("foo", propertyResolver.apply("foo"), "Non-expression");
    assertProperty("bar", "foo", "Use existing value");
    assertProperty("gnat", "baz", "Substitute with value");
    assertProperty("quux", "qux", "Substitute with default if not found");
    assertProperty("", "waldo", "Substitute with empty default");
    assertProperty("${grault}", "corge", "Don't substitute if not found and no default");
    assertProperty("hamANDeggs", "spam", "Keep prefix and suffix");
    assertProperty("http://localhost:8765/services", "url", "Multiple subsitutions");
    assertProperty("http://localhost:8765/services", "url", "Multiple subsitutions");
    assertPropertyExpression("bottle", "${empty:bottle}", "Empty value should not override");
  }

  private void assertProperty(String expected, String name, String message) {
    assertPropertyExpression(expected, String.format("${%s}", name), message);
  }

  private void assertPropertyExpression(String expected, String expression, String message) {
    assertEquals(expected, propertyResolver.apply(expression), message);
  }

  @Test
  public void shouldOverrideParentProperties() {
    propertyResolver = new ConfigurationProperties(resourceResolver, "configuration.properties",
        new ConfigurationProperties(resourceResolver, "1.properties",
            new ConfigurationProperties(resourceResolver, "0.properties")));

    assertProperty("thud", "qux", "From parent");
    assertProperty("garply", "corge", "From grandparent");
  }

  @Test
  public void shouldTrimValues() {
    propertyResolver = new ConfigurationProperties(resourceResolver, "configuration.properties");
    assertEquals("bar", propertyResolver.apply("${foo}"), "Trimmed value");
  }

}
