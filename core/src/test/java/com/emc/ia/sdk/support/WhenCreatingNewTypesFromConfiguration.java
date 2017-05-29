/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.emc.ia.sdk.support.test.RandomData;

public class WhenCreatingNewTypesFromConfiguration {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private final RandomData random = new RandomData();

  @Test
  public void shouldReturnInstanceOfConfiguredClassAsRequestedType() {
    String option = random.string(16);
    Class<?> type = HashMap.class; // NOPMD LooseCoupling
    Map<String, String> configuration = Collections.singletonMap(option, type.getName());

    Map<?, ?> instance = NewInstance.fromConfiguration(configuration, option, null)
      .as(Map.class);

    assertEquals("Type", type, instance.getClass());
  }

  @Test
  public void shouldReturnInstanceOfDefaultClassWhenConfigurationIsMissing() {
    Class<?> type = HashMap.class; // NOPMD LooseCoupling

    Map<?, ?> instance = NewInstance.fromConfiguration(Collections.emptyMap(), "", type.getName())
      .as(Map.class);

    assertEquals("Type", type, instance.getClass());
  }

  @Test
  public void shouldThrowRuntimeExceptionWhenClassCantBeInstantiated() {
    String option = random.string(16);
    Class<Integer> type = Integer.class;

    thrown.expect(RuntimeException.class);
    NewInstance.fromConfiguration(Collections.singletonMap(option, type.getName()), option, null)
      .as(type);
  }

}
