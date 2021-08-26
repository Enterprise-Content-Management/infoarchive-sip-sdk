/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.test.RandomData;


class WhenWorkingWithSearchResults {

  private RandomData data;

  @BeforeEach
  public void before() {
    data = new RandomData();
  }

  @Test
  void shouldHaveNoDefaults() {
    Column column = new Column();
    assertNull(column.getName());
    assertNull(column.getValue());
  }

  @Test
  void shouldUpdateStateAfterSettersAreCalled() {
    String name = data.string();
    String value = data.string();
    Column column = new Column();

    column.setName(name);
    column.setValue(value);
    assertEquals(name, column.getName());
    assertEquals(value, column.getValue());
  }

}
