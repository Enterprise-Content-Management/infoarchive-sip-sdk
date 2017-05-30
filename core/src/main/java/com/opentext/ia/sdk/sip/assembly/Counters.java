/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.assembly;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Set of related counters.
 */
public class Counters {

  private final Map<String, Long> values;

  public Counters() {
    this(new HashMap<>());
  }

  Counters(Map<String, Long> values) {
    this.values = values;
  }

  public long get(String name) {
    return values.containsKey(name) ? values.get(name) : 0;
  }

  /**
   * Increase the metric with the given name by one.
   * @param name The name of the metric
   */
  public void inc(String name) {
    inc(name, 1);
  }

  /**
   * Increase the metric with the given name by the given amount.
   * @param name The name of the metric
   * @param delta The amount to increase the metric with
   */
  public void inc(String name, long delta) {
    set(name, get(name) + delta);
  }

  /**
   * Set the metric with the given name to the given value.
   * @param name The name of the metric
   * @param value The value to set the metric to
   */
  public void set(String name, long value) {
    values.put(name, value);
  }

  /**
   * Reset all metrics to zero.
   */
  public void reset() {
    values.clear();
  }

  public Counters forReading() {
    return new Counters(Collections.unmodifiableMap(new HashMap<String, Long>(values)));
  }

  @Override
  public String toString() {
    return values.toString();
  }

}
