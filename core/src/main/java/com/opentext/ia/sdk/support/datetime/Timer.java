/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.datetime;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Run a recurring process.
 */
public class Timer {

  private static final String TASK_NAME = "Timer_" + UUID.randomUUID();

  private final long millis;
  private final Runnable process;
  private final Clock clock;
  private final Runnable ring = this::ring; // Create only one instance of this lambda

  /**
   * Create the timer using the default clock.
   * @param millis The interval at which to run the recurring process
   * @param process The process to run
   */
  public Timer(long millis, Runnable process) {
    this(millis, process, new DefaultClock());
  }

  /**
   * Create the timer using the provided clock.
   * @param millis The interval at which to run the recurring process
   * @param process The process to run
   * @param clock The clock to use
   */
  public Timer(long millis, Runnable process, Clock clock) {
    if (millis <= 0) {
      throw new IllegalArgumentException("millis must be positive");
    }
    this.millis = millis;
    this.process = Objects.requireNonNull(process, "Missing process");
    this.clock = Objects.requireNonNull(clock, "Missing clock");
    start();
  }

  private void start() {
    clock.schedule(TASK_NAME, millis, TimeUnit.MILLISECONDS, ring);
  }

  private void ring() {
    try {
      process.run();
    } finally {
      start();
    }
  }

  /**
   * Restart the timer.
   */
  public void reset() {
    stop();
    start();
  }

  /**
   * Stop running the process.
   */
  public void stop() {
    clock.cancel(TASK_NAME);
  }

}
