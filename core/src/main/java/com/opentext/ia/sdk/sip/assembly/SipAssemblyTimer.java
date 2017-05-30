/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.assembly;

import java.util.function.Consumer;

import com.opentext.ia.sdk.support.datetime.Clock;
import com.opentext.ia.sdk.support.datetime.DefaultClock;

public class SipAssemblyTimer {

  private final long millis;
  private final Clock clock;
  private final Consumer<FileGenerationMetrics> callback;

  public SipAssemblyTimer(long millis, Consumer<FileGenerationMetrics> callback) {
    this(millis, new DefaultClock(), callback);
  }

  public SipAssemblyTimer(long millis, Clock clock, Consumer<FileGenerationMetrics> callback) {
    this.millis = millis;
    this.clock = clock;
    this.callback = callback;
  }

  public long getMillis() {
    return millis;
  }

  public Clock getClock() {
    return clock;
  }

  public Consumer<FileGenerationMetrics> getCallback() {
    return callback;
  }

}
