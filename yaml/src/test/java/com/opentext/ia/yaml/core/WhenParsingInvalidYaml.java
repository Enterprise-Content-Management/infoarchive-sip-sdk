/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.yaml.snakeyaml.error.MarkedYAMLException;


public class WhenParsingInvalidYaml {

  private static final String INCORRECT_INDENTATION = "Incorrect indentation";

  @Test
  public void shouldReportTooManySpacesAfterSequence() {
    assertInvalidYaml(INCORRECT_INDENTATION, "foo:%n- bar: baz%n   gnu: gnat");
  }

  private void assertInvalidYaml(String expected, String format, Object... args) {
    try {
      String yaml = String.format(format, args);
      YamlMap map = YamlMap.from(yaml);
      fail("No exception for invalid YAML; " + yaml + " ->\n" + map);
    } catch (MarkedYAMLException e) {
      assertTrue("Problem doesn't contain '" + expected + "': " + e.getProblem(), e.getProblem().contains(expected));
    }
  }

  @Test
  public void shouldReportTooFewSpacesAfterSequence() {
    assertInvalidYaml(INCORRECT_INDENTATION, "foo:%n- bar: baz%n gnu: gnat");
  }

  @Test
  public void shouldReportTooFewSpacesAfterNestedSequence() {
    assertInvalidYaml(INCORRECT_INDENTATION, "foo:%n  bar:%n    - baz: gnu%n waldo: fred");
  }

  @Test
  public void shouldReportInvalidBlock() {
    assertInvalidYaml(INCORRECT_INDENTATION, "foo:%n  bar:%n gnu: gnat");
  }

  @Test
  public void shouldReportItemOutsideSequence() {
    assertInvalidYaml("Item outside of sequence", "foo: bar%n- baz: gnu");
  }

}
