/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class WhenMaintainingCounters {

  private final Counters counters = new Counters();

  @Test
  public void shouldIncreaseCounter() {
    assertEquals(0, counters.get(TestMetrics.FOO), "Initial value");

    counters.inc(TestMetrics.FOO);
    assertEquals(1, counters.get(TestMetrics.FOO), "Incremented value");
  }

  @Test
  public void shouldClearCounterOnReset() {
    counters.inc(TestMetrics.FOO);
    counters.inc(TestMetrics.BAR);

    counters.reset();

    assertEquals(0, counters.get(TestMetrics.FOO), "Reset foo");
    assertEquals(0, counters.get(TestMetrics.BAR), "Reset bar");
  }

}
