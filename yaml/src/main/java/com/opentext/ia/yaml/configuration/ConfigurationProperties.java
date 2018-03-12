/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opentext.ia.yaml.resource.ResourceResolver;


public class ConfigurationProperties implements Function<String, String> {

  private static final String SUBSTITUTE_START = "${";
  private static final Pattern REFERENCE = Pattern.compile(
      "\\$\\{(?<var>[^}:]+)(:(?<default>[^}]*))?\\}(?<suffix>[^$]+)?");

  private final ConfigurationProperties parent;
  private final Properties properties = new Properties();

  public ConfigurationProperties() {
    this.parent = null;
  }

  public ConfigurationProperties(ResourceResolver resolver, String resource) {
    this(resolver, resource, null);
  }

  public ConfigurationProperties(ResourceResolver resolver, String resource, ConfigurationProperties parent) {
    this.parent = parent;
    try {
      properties.load(new StringReader(resolver.apply(resource)));
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load properties from " + resource, e);
    }
  }

  @Override
  public String apply(String expression) {
    return containsProperty(expression) ? substituteAll(expression) : expression;
  }

  private boolean containsProperty(String expression) {
    return expression.contains(SUBSTITUTE_START);
  }

  private String substituteAll(String expression) {
    StringBuilder result = new StringBuilder();
    int current = 0;
    while (current >= 0) {
      int next = expression.indexOf(SUBSTITUTE_START, current + 1);
      String part = next < 0 ? expression.substring(current) : expression.substring(current, next);
      result.append(substitute(part));
      current = next;
    }
    return result.toString();
  }

  private String substitute(String expression) {
    Matcher matcher = REFERENCE.matcher(expression);
    if (!matcher.matches()) {
      return expression;
    }
    String substitution = resolve(matcher.group("var"));
    if (substitution == null) {
      substitution = matcher.group("default");
    }
    if (substitution == null) {
      return expression;
    }
    StringBuilder result = new StringBuilder(substitution);
    String suffix = matcher.group("suffix");
    if (suffix != null) {
      result.append(suffix);
    }
    return result.toString();
  }

  private String resolve(String expressionOrName) {
    String expression = containsProperty(expressionOrName) ? expressionOrName : lookup(expressionOrName);
    return expression == null ? null : substituteAll(expression);
  }

  private String lookup(String name) {
    String result = properties.getProperty(name);
    if (result != null && result.trim().isEmpty()) {
      result = null;
    }
    if (result == null && parent != null) {
      result = parent.lookup(name);
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    Collections.list(properties.propertyNames()).forEach(name ->
        result.append(name).append('=').append(properties.get(name)).append(System.lineSeparator()));
    if (parent != null) {
      result.append("-----").append(System.lineSeparator()).append(parent);
    }
    return result.toString();
  }

}
