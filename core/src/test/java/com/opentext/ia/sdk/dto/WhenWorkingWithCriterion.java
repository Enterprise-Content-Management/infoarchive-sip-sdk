/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.test.RandomData;
import com.opentext.ia.test.TestCase;


class WhenWorkingWithCriterion extends TestCase {

  private String label;
  private String name;
  private String pKeyMaxAttr;
  private String pKeyMinAttr;
  private String pKeyValuesAttr;
  private String type;

  private final RandomData random = new RandomData();

  @BeforeEach
  public void before() {
    label = random.string();
    name = random.string();
    pKeyMaxAttr = random.string();
    pKeyMinAttr = random.string();
    pKeyValuesAttr = random.string();
    type = random.string();
  }

  @Test
  void shouldHaveNoDefaults() {
    Criterion criterion = new Criterion();
    assertNull(criterion.getLabel());
    assertNull(criterion.getName());
    assertNull(criterion.getpKeyMaxAttr());
    assertNull(criterion.getpKeyMinAttr());
    assertNull(criterion.getpKeyValuesAttr());
    assertNull(criterion.getType());
    assertFalse(criterion.isIndexed());
  }

  @Test
  void shouldHonorSetValues() {
    Criterion criterion = newCriterion();

    assertEquals(label, criterion.getLabel());
    assertEquals(name, criterion.getName());
    assertEquals(pKeyMaxAttr, criterion.getpKeyMaxAttr());
    assertEquals(pKeyMinAttr, criterion.getpKeyMinAttr());
    assertEquals(pKeyValuesAttr, criterion.getpKeyValuesAttr());
    assertEquals(type, criterion.getType());
    assertTrue(criterion.isIndexed());

  }

  @Test
  void shouldHaveDescriptiveToString() {
    Criterion criterion = newCriterion();

    String string = criterion.toString();
    assertTrue(string.contains(label));
    assertTrue(string.contains(name));
    assertTrue(string.contains(type));
    assertTrue(string.contains(pKeyMaxAttr));
    assertTrue(string.contains(pKeyMinAttr));
  }

  private Criterion newCriterion() {
    Criterion criterion = new Criterion();
    criterion.setIndexed(true);
    criterion.setLabel(label);
    criterion.setName(name);
    criterion.setpKeyMaxAttr(pKeyMaxAttr);
    criterion.setpKeyMinAttr(pKeyMinAttr);
    criterion.setpKeyValuesAttr(pKeyValuesAttr);
    criterion.setType(type);
    return criterion;
  }

}
