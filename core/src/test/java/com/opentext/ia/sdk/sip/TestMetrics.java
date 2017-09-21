/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;


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
