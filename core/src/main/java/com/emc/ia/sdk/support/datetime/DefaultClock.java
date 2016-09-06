/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.datetime;

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
      // Ignore
    }
  }

  @Override
  public long time() {
    return System.currentTimeMillis();
  }

  @Override
  public void schedule(String name, long time, TimeUnit unit, Runnable task) {
    Timer timer = new Timer();
    timers.put(name, timer);
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        try {
          timers.remove(name)
            .cancel();
        } finally {
          task.run();
        }
      }
    }, unit.toMillis(time));
  }

  @Override
  public void cancel(String name) {
    Timer timer = timers.remove(name);
    if (timer != null) {
      timer.cancel();
    }
  }

}
