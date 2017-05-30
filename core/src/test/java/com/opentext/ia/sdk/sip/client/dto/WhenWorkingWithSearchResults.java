/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.opentext.ia.sdk.support.test.RandomData;


public class WhenWorkingWithSearchResults {

  private RandomData data;

  @Before
  public void before() {
    data = new RandomData();
  }

  @Test
  public void shouldHaveNoDefaults() {
    Column column = new Column();
    assertNull(column.getName());
    assertNull(column.getValue());
  }

  @Test
  public void shouldUpdateStateAfterSettersAreCalled() {
    String name = data.string();
    String value = data.string();
    Column column = new Column();

    column.setName(name);
    column.setValue(value);
    assertEquals(name, column.getName());
    assertEquals(value, column.getValue());
  }

}
