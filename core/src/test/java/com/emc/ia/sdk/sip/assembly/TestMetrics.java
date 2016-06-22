/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;


class TestMetrics implements Metrics {

  static final String FOO = "foo";
  static final String BAR = "bar";

  private final Counters counters;

  TestMetrics(Counters counters) {
    this.counters = counters;
  }

  long getFoo() {
    return counters.get(FOO);
  }

}
