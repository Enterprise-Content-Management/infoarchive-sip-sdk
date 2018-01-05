/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.datetime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Default implementation of {@linkplain Clock}.
 */
public class DefaultClock implements Clock {

  private final Map<String, Timer> timers = Collections.synchronizedMap(new HashMap<>());

  @Override
  public void sleep(long time, TimeUnit unit) {
    try {
      unit.sleep(time);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public long time() {
    return System.currentTimeMillis();
  }

  @Override
  public void schedule(String name, long time, TimeUnit unit, Runnable task) {
    Timer timer = new Timer(true);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        try {
          DefaultClock.this.cancel(name);
        } finally {
          task.run();
        }
      }
    }, unit.toMillis(time));
    timers.put(name, timer);
  }

  @Override
  public void cancel(String name) {
    Timer timer = timers.remove(name);
    if (timer != null) {
      timer.cancel();
    }
  }

}
