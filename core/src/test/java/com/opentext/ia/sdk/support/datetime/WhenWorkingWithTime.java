/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.datetime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.opentext.ia.test.RandomData;


class WhenWorkingWithTime {

  private static final int TASK_WAIT_DELTA = 200;

  private final RandomData random = new RandomData();
  private final Clock clock = new DefaultClock();

  @Test
  void shouldSleepForSpecifiedTime() {
    int expected = random.integer(10, 20);
    long start = System.currentTimeMillis();

    clock.sleep(expected, TimeUnit.MILLISECONDS);

    long actual = System.currentTimeMillis() - start;
    assertTrue(actual > 0, "Did not sleep: " + actual);
  }

  @Test
  void shouldTellTimeAccurately() {
    long delta = Math.abs(System.currentTimeMillis() - clock.time());
    assertTrue(delta <= 5, "Clock deviates from real time by " + delta);
  }

  @Test
  void shouldScheduleTaskForLaterExecution() throws InterruptedException {
    int sleep = random.integer(20, 30);
    final AtomicBoolean executed = new AtomicBoolean(false);
    clock.schedule(random.string(), sleep, TimeUnit.MILLISECONDS, () -> executed.set(true));
    assertFalse(executed.get(), "Task run right away");

    Thread.sleep(sleep + TASK_WAIT_DELTA);
    assertTrue(executed.get(), "Task not run after specified time");
  }

  @Test
  void shouldCancelScheduledTask() throws InterruptedException {
    int sleep = random.integer(2, 10);
    String name = random.string();
    final AtomicBoolean executed = new AtomicBoolean(false);
    clock.schedule(name, sleep, TimeUnit.MILLISECONDS, () -> executed.set(true));

    clock.cancel(name);

    Thread.sleep(sleep + TASK_WAIT_DELTA);
    assertFalse(executed.get(), "Canceled task is run");
  }

  /**
   * The task may have just run and be removed automatically.
   */
  @Test
  void shouldSilentlyIgnoreCancellingAnUnknowTask() {
    clock.cancel(random.string());
  }

}
