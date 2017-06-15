/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.yaml;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.opentext.ia.sdk.support.test.TestCase;


public class WhenWorkingWithYamlInAGenericYetTypeSafeManner extends TestCase {

  private final YamlMap yaml = new YamlMap();
  private final String key = someValue();
  private final String value = someValue();

  private String someValue() {
    return randomString(10);
  }

  @Test
  public void shouldStartEmpty() {
    assertTrue("Not empty", yaml.isEmpty());
    assertEquals("Size", 0, yaml.size());
    assertTrue("Value found", yaml.get(someValue()).isEmpty());
  }

  @Test
  public void shouldBeAbleToAddItems() {
    yaml.put(key, value);

    assertFalse("Empty", yaml.isEmpty());
    assertEquals("Size", 1, yaml.size());
    assertValue();
  }

  private void assertValue() {
    assertValue(value);
  }

  private void assertValue(Object expected) {
    assertValue(expected, yaml);
  }

  private void assertValue(Object expected, YamlMap map) {
    assertValue(expected, map.get(key));
  }

  private void assertValue(Object expected, Value actual) {
    if (!actual.equals(expected)) {
      assertEquals("Value", expected, actual);
    }
  }

  @Test
  public void shouldLoadFromOtherMap() {
    YamlMap map = new YamlMap(Collections.singletonMap(key, value));
    assertValue(value, map);

    yaml.putAll(map);
    assertValue();
  }

  @Test
  public void shouldLoadValueFromOtherMap() {
    YamlMap map = new YamlMap().put(key, value);
    Value v = map.get(key);

    yaml.put(key, v);

    assertValue();
    assertValue(v);
  }

  @Test
  public void shouldRemoveValue() {
    assertFalse("Contains non-added value", yaml.containsKey(key));

    yaml.put(key, value);
    assertTrue("Doesn't contain added value", yaml.containsKey(key));

    yaml.remove(key);
    assertFalse("Still contains removed value", yaml.containsKey(key));
  }

  @Test
  public void shouldRetrieveNestedValues() {
    String outerKey = someValue();
    YamlMap map = new YamlMap(Collections.singletonMap(outerKey, Collections.singletonMap(key, value)));

    assertValue(value, map.get(outerKey, key));
  }

  @Test
  public void shouldIterateOverEntriesAndValues() {
    String key1 = 'z' + someValue();
    String value1 = someValue();
    String key2 = 'a' + someValue();
    String value2 = someValue();
    yaml.put(key1, value1);
    yaml.put(key2, value2);

    assertEquals("Keys", key2 + ',' + key1, yaml.entries().map(Entry::getKey).collect(Collectors.joining(",")));
    assertEquals("Values", value2 + ',' + value1, yaml.values().map(Value::toString).collect(Collectors.joining(",")));
  }

  @Test
  public void shouldExtractNamedNestedObject() {
    String name = someValue();
    yaml.put(name, new YamlMap().put(key, value));

    YamlMap nestedObject = yaml.entries()
        .map(Entry::toMap)
        .findFirst()
        .get();

    assertEquals("Name", name, nestedObject.get("name").toString());
    assertEquals("Property", value, nestedObject.get(key).toString());
  }

  @Test
  public void shouldCheckWhetherValueExists() {
    yaml.put(key, null);
    assertTrue("Null value", yaml.get(key).isEmpty());

    yaml.put(key, value);
    assertFalse("Still null value", yaml.get(key).isEmpty());
  }

  @Test
  public void shouldConvertValueToMap() {
    yaml.put(key, value);
    assertFalse("String is a map", yaml.get(key).isMap());

    yaml.put(key, new YamlMap().put(key, value));
    Value v = yaml.get(key);
    assertTrue("Map is not a map", v.isMap());
    YamlMap map = v.toMap();
    assertValue(value, map);

    String value2 = someValue();
    map.put(key, value2);
    assertValue(value2, yaml.get(key, key));
  }

  @Test
  public void shouldConvertValueToList() {
    assertTrue("Empty by default", yaml.get(key).toList().isEmpty());
    yaml.put(key, value);
    assertFalse("String is a list", yaml.get(key).isList());

    String value2 = someValue();
    yaml.put(key, Arrays.asList(value, value2));
    Value v = yaml.get(key);
    assertTrue("List is not a list", v.isList());
    List<Value> values = v.toList();
    assertValue(value, values.get(0));
    assertValue(value2, values.get(1));
  }

  @Test
  public void shouldConvertValueToBoolean() {
    assertFalse("Empty", yaml.get(key).toBoolean());

    yaml.put(key, true);
    assertTrue("Boolean", yaml.get(key).toBoolean());

    yaml.put(key, Boolean.toString(true));
    assertTrue("Boolean string", yaml.get(key).toBoolean());
  }

}
