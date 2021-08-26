/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;


public class WhenDiffingYaml {

  private static final YamlMap EMPTY = new YamlMap();
  private static final String SEPARATOR = "\n---\n";
  private static final String NAME1 = "foo";
  private static final String NAME2 = "bar";
  private static final String NAME3 = "corge";
  private static final String VALUE1 = "baz";
  private static final String VALUE2 = "qux";
  private static final String VALUE3 = "zuul";

  @Test
  public void shouldReportNothingForEqualYamls() {
    YamlMap yaml = new YamlMap()
        .put(NAME1, VALUE1)
        .put(NAME2, new YamlMap().put(NAME1, VALUE1).put(NAME2, Collections.singletonList(VALUE3)))
        .put(NAME3, Arrays.asList(VALUE1, new YamlMap().put(NAME2, VALUE2), Collections.singletonList(VALUE3)));

    assertDiff(EMPTY, EMPTY, yaml, yaml);
  }

  private void assertDiff(YamlMap expectedLeft, YamlMap expectedRight, YamlMap left, YamlMap right) {
    YamlDiff diff = new YamlDiff(left, right);
    assertEquals(diffOf(expectedLeft, expectedRight), diffOf(diff.left(), diff.right()));
  }

  private String diffOf(YamlMap left, YamlMap right) {
    return left.toString().trim() + SEPARATOR + right.toString().trim();
  }

  @Test
  public void shouldReportDifferentPropertiesButNotEqualOnes() {
    assertDiff(
        new YamlMap().put(NAME2, VALUE2),
        new YamlMap().put(NAME2, VALUE3),
        new YamlMap().put(NAME1, VALUE1).put(NAME2, VALUE2),
        new YamlMap().put(NAME1, VALUE1).put(NAME2, VALUE3));
  }

  @Test
  public void shouldReportMissingProperties() {
    assertDiff(
        new YamlMap().put(NAME1, YamlDiff.NO_VALUE),
        new YamlMap().put(NAME1, VALUE1),
        EMPTY,
        new YamlMap().put(NAME1, VALUE1));
  }

  @Test
  public void shouldCompareNullProperly() {
    YamlMap mapWithNull = new YamlMap().put(NAME1, null);
    YamlMap mapWithValue = new YamlMap().put(NAME1, VALUE1);
    assertDiff(mapWithNull, mapWithValue, mapWithNull, mapWithValue);
  }

  @Test
  public void shouldCompareMapsOnlyWithMaps() {
    YamlMap mapWithValue = new YamlMap().put(NAME1, VALUE1);
    YamlMap mapWithMap = new YamlMap().put(NAME1, new YamlMap().put(NAME2, VALUE2));
    assertDiff(mapWithValue, mapWithMap, mapWithValue, mapWithMap);
    assertDiff(mapWithMap, mapWithValue, mapWithMap, mapWithValue);
  }

  @Test
  public void shouldRecurseIntoMaps() {
    assertDiff(
        new YamlMap().put(NAME1, new YamlMap().put(NAME2, VALUE1)),
        new YamlMap().put(NAME1, new YamlMap().put(NAME2, VALUE2)),
        new YamlMap().put(NAME1, new YamlMap().put(NAME2, VALUE1).put(NAME3, new YamlMap().put(NAME1, VALUE3))),
        new YamlMap().put(NAME1, new YamlMap().put(NAME2, VALUE2).put(NAME3, new YamlMap().put(NAME1, VALUE3))));
  }

  @Test
  public void shouldCompareListsOnlyWithLists() {
    YamlMap mapWithValue = new YamlMap().put(NAME1, VALUE1);
    YamlMap mapWithList = new YamlMap().put(NAME1, Collections.singletonList(VALUE2));
    assertDiff(mapWithValue, mapWithList, mapWithValue, mapWithList);
    assertDiff(mapWithList, mapWithValue, mapWithList, mapWithValue);
  }

  @Test
  public void shouldRecursiveIntoLists() {
    assertDiff(
        new YamlMap().put(NAME1, Arrays.asList(YamlDiff.SAME_VALUE, VALUE2)),
        new YamlMap().put(NAME1, Arrays.asList(YamlDiff.SAME_VALUE, VALUE3)),
        new YamlMap().put(NAME1, Arrays.asList(VALUE1, VALUE2, VALUE3)),
        new YamlMap().put(NAME1, Arrays.asList(VALUE1, VALUE3, VALUE3)));
  }

}
