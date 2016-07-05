/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
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

public class WhenReadingRepeatingConfigurationGroups {

  private String name;
  private RandomData random = new RandomData();
  private Map<String, String> configuration;
  private List<String> fields = Arrays.asList("field1", "field2", "field3");

  @Before
  public void before() {
    name = random.string();
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
  public void shouldThrowExceptionIfMismatchedGroup() {
    configuration.put("field1", "one,two");
    configuration.put("field2", "onlyone");
    configuration.put("field3", "1,2");
    RepeatingConfigReader reader = new RepeatingConfigReader(name, fields);
    reader.read(configuration);
  }

  @Test
  public void shouldReturnRepeatingGroupAsListOfMap() {
    configuration.put("field1", "one,two");
    configuration.put("field2", "a,b");
    configuration.put("field3", "1,2");
    RepeatingConfigReader reader = new RepeatingConfigReader(name, fields);
    List<Map<String, String>> result = reader.read(configuration);

    assertNotNull(result);
    assertEquals(2, result.size());

    Map<String, String> group1 = result.get(0);
    Map<String, String> group2 = result.get(1);
    assertEquals("one", group1.get("field1"));
    assertEquals("a", group1.get("field2"));
    assertEquals("1", group1.get("field3"));
    assertEquals("two", group2.get("field1"));
    assertEquals("b", group2.get("field2"));
    assertEquals("2", group2.get("field3"));
  }

}
