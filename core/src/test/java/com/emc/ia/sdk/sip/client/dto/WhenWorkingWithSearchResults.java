/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.test.RandomData;

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
