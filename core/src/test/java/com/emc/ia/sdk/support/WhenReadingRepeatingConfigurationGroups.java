/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;

public class WhenReadingRepeatingConfigurationGroups extends TestCase {

  private static final String FIELD3 = "field3";
  private static final String FIELD2 = "field2";
  private static final String FIELD1 = "field1";

  private String name;
  private Map<String, String> configuration;
  private final List<String> fields = Arrays.asList(FIELD1, FIELD2, FIELD3);

  @Before
  public void before() {
    name = randomString();
    configuration = new HashMap<>();
  }

  @Test
  public void shouldReturnEmptyListIfMissingConfiguration() {
    RepeatingConfigReader reader = new RepeatingConfigReader(name, fields);
    List<Map<String, String>> result = reader.read(Collections.emptyMap());
    assertNotNull(result);

    assertTrue(result.isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfNullName() {
    new RepeatingConfigReader(null, fields);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfBlankName() {
    new RepeatingConfigReader("", fields);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfNullFields() {
    new RepeatingConfigReader(name, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfEmptyFields() {
    new RepeatingConfigReader(name, Collections.emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowExceptionIfMismatchedGroup() {
    configuration.put(FIELD1, "one,two");
    configuration.put(FIELD2, "onlyone");
    configuration.put(FIELD3, "1,2");
    RepeatingConfigReader reader = new RepeatingConfigReader(name, fields);
    reader.read(configuration);
  }

  @Test
  public void shouldReturnRepeatingGroupAsListOfMap() {
    configuration.put(FIELD1, "one,two");
    configuration.put(FIELD2, "a,b");
    configuration.put(FIELD3, "1,2");
    RepeatingConfigReader reader = new RepeatingConfigReader(name, fields);
    List<Map<String, String>> result = reader.read(configuration);

    assertNotNull(result);
    assertEquals(2, result.size());

    Map<String, String> group1 = result.get(0);
    Map<String, String> group2 = result.get(1);
    assertEquals("one", group1.get(FIELD1));
    assertEquals("a", group1.get(FIELD2));
    assertEquals("1", group1.get(FIELD3));
    assertEquals("two", group2.get(FIELD1));
    assertEquals("b", group2.get(FIELD2));
    assertEquals("2", group2.get(FIELD3));
  }

}
