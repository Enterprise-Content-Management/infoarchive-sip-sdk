/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import com.emc.ia.sdk.support.datetime.Clock;


public class MaxTimePerSipSegmentationStrategy<D> implements SipSegmentationStrategy<D> {

  private final long maxTime;
  private final Clock clock;
  private long start;

  public MaxTimePerSipSegmentationStrategy(long maxTime, Clock clock) {
    this.maxTime = maxTime;
    this.clock = clock;
    this.start = clock.time();
  }

  @Override
  public boolean shouldStartNewSip(D domainObject, SipMetrics metrics) {
    boolean result = clock.time() - start >= maxTime;
    if (result) {
      start = clock.time();
    }
    return result;
  }

}
