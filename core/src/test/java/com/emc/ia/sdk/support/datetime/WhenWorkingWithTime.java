/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.datetime;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.emc.ia.sdk.support.RandomData;


public class WhenWorkingWithTime {

  private static final int TASK_WAIT_DELTA = 100;

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  private final RandomData random = new RandomData();
  private final Clock clock = new DefaultClock();

  @Test
  public void shouldSleepForSpecifiedTime() {
    int expected = random.integer(10, 20);
    long start = System.currentTimeMillis();

    clock.sleep(expected, TimeUnit.MILLISECONDS);

    long actual = System.currentTimeMillis() - start;
    assertTrue("Did not sleep: " + actual, actual > 0);
  }

  @Test
  public void shouldTellTimeAccurately() {
    long delta = Math.abs(System.currentTimeMillis() - clock.time());
    assertTrue("Clock deviates from real time by " + delta, delta <= 5);
  }

  @Test
  public void shouldScheduleTaskForLaterExecution() throws InterruptedException {
    int sleep = random.integer(20, 30);
    final AtomicBoolean executed = new AtomicBoolean(false);
    clock.schedule(random.string(), sleep, TimeUnit.MILLISECONDS, () -> executed.set(true));
    assertFalse("Task run right away", executed.get());

    Thread.sleep(sleep + TASK_WAIT_DELTA);
    assertTrue("Task not run after specified time", executed.get());
  }

  @Test
  public void shouldCancelScheduledTask() throws InterruptedException {
    int sleep = random.integer(2, 10);
    String name = random.string();
    final AtomicBoolean executed = new AtomicBoolean(false);
    clock.schedule(name, sleep, TimeUnit.MILLISECONDS, () -> executed.set(true));

    clock.cancel(name);

    Thread.sleep(sleep + TASK_WAIT_DELTA);
    assertFalse("Canceled task is run", executed.get());
  }

  @Test
  public void shouldThrowExceptionWhenCancellingAnUnknowTask() {
    String name = random.string();

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Unknown scheduled task: " + name);
    clock.cancel(name);
  }

}
