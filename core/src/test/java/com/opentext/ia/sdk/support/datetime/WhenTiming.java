/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.datetime;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.opentext.ia.test.TestCase;


class WhenTiming extends TestCase {

  private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

  private final Clock clock = mock(Clock.class);
  private final Runnable callback = mock(Runnable.class);
  private long maxTime;
  private Timer timer;
  private String taskName;
  private Runnable ringer;

  @BeforeEach
  public void init() {
    maxTime = randomInt(37, 313);
    timer = new Timer(maxTime, callback, clock);
    ArgumentCaptor<String> taskNameCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(clock).schedule(taskNameCaptor.capture(), eq(maxTime), eq(TIME_UNIT), taskCaptor.capture());
    taskName = taskNameCaptor.getValue();
    ringer = taskCaptor.getValue();
  }

  @Test
  void shouldRestartTimerWhenReset() {
    timer.reset();

    verify(clock).cancel(taskName);
    verify(callback, never()).run();
    verify(clock, times(2)).schedule(taskName, maxTime, TIME_UNIT, ringer);
  }

  @Test
  void shouldCallBackWhenTimePasses() {
    ringer.run();

    verify(clock, never()).cancel(taskName);
    verify(callback).run();
    verify(clock, times(2)).schedule(taskName, maxTime, TIME_UNIT, ringer);
  }

  @Test
  void shouldCancelTimerWhenStopped() {
    timer.stop();

    verify(clock).cancel(taskName);
    verify(callback, never()).run();
    verify(clock, times(1)).schedule(taskName, maxTime, TIME_UNIT, ringer);
  }

}
