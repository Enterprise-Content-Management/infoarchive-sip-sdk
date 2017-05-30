/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.datetime;

import java.util.concurrent.TimeUnit;

/**
 * Time-telling device.
 */
public interface Clock {

  /**
   * Sleep for some time.
   * @param time The time to sleep
   * @param unit The unit of the provided time
   */
  void sleep(long time, TimeUnit unit);

  /**
   * Return the current time.
   * @return The current time
   */
  long time();

  /**
   * Schedule a task to run later.
   * @param name The name of the task
   * @param time The time to wait before running the task
   * @param unit The unit of the provided time
   * @param task The task to schedule
   */
  void schedule(String name, long time, TimeUnit unit, Runnable task);

  /**
   * Cancel a scheduled job.
   * @param name The name of the scheduled task, as given to {@linkplain #schedule(String, long, TimeUnit, Runnable)}
   */
  void cancel(String name);

}
