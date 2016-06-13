/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.assembly;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class WhenMaintainingCounters {

  private final Counters counters = new Counters();

  @Test
  public void shouldIncreaseCounter() {
    assertEquals("Initial value", 0, counters.get(TestMetrics.FOO));

    counters.inc(TestMetrics.FOO);
    assertEquals("Incremented value", 1, counters.get(TestMetrics.FOO));
  }

  @Test
  public void shouldClearCounterOnReset() {
    counters.inc(TestMetrics.FOO);
    counters.inc(TestMetrics.BAR);

    counters.reset();

    assertEquals("Reset foo", 0, counters.get(TestMetrics.FOO));
    assertEquals("Reset bar", 0, counters.get(TestMetrics.BAR));
  }

}
