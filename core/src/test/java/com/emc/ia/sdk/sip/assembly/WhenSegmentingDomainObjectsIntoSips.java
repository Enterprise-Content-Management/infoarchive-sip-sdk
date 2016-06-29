/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import static org.junit.Assert.*;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenSegmentingDomainObjectsIntoSips extends TestCase {

  private SipSegmentationStrategy<String> strategy;
  private int expected;

  @Before
  public void init() {
    expected = randomInt(3, 7);
  }

  @Test
  public void shouldSegmentByNumberOfAius() {
    assertMaxCountPerSip(SipMetrics.NUM_AIUS, max -> SipSegmentationStrategy.byMaxAius(max));
  }

  private void assertMaxCountPerSip(String counter, Function<Integer, SipSegmentationStrategy<String>> factory) {
    int maxCount = randomInt(2, 5);
    strategy = factory.apply(maxCount);
    assertNumSips(expected * maxCount, counters -> counters.inc(counter));
  }

  private void assertNumSips(int numObjects, Consumer<Counters> countersUpdater) {
    Counters metrics = new Counters();
    int actual = 0;
    for (int i = 0; i < numObjects; i++) {
      if (strategy.shouldStartNewSip(randomString(), new SipMetrics(metrics))) {
        actual++;
      }
      countersUpdater.accept(metrics);
    }
    assertEquals("# SIPs", expected, actual);
  }

  @Test
  public void shouldSegmentByNumberOfDigitalObjects() {
    assertMaxCountPerSip(SipMetrics.NUM_DIGITAL_OBJECTS, max -> SipSegmentationStrategy.byMaxDigitalObjects(max));
  }

  @Test
  public void shouldSegmentByPdiSize() {
    assertMaxSizePerSip(SipMetrics.SIZE_PDI, max -> SipSegmentationStrategy.byMaxPdiSize(max));
  }

  private void assertMaxSizePerSip(String size, Function<Integer, SipSegmentationStrategy<String>> factory) {
    int sizeIncrement = randomInt(2, 5);
    int numPerSip = randomInt(3, 6);
    int maxSize = numPerSip * sizeIncrement;
    strategy = factory.apply(maxSize);
    assertNumSips(expected * numPerSip + 1, counter -> {
      if (counter.get(size) >= maxSize) {
        counter.set(size, sizeIncrement);
      } else {
        counter.inc(size, sizeIncrement);
      }
    });
  }

  @Test
  public void shouldSegmentByDigitalObjectsSize() {
    assertMaxSizePerSip(SipMetrics.SIZE_DIGITAL_OBJECTS, max -> SipSegmentationStrategy.byMaxDigitalObjectsSize(max));
  }

  @Test
  public void shouldSegmentBySipSize() {
    assertMaxSizePerSip(SipMetrics.SIZE_SIP, max -> SipSegmentationStrategy.byMaxSipSize(max));
  }

  @Test
  public void shouldCombineSegmentations() {
    String object1 = randomString();
    String object2 = randomString();
    String object3 = randomString();

    strategy = SipSegmentationStrategy.combining(segmentOn(object1), segmentOn(object2), segmentOn(object3));

    assertTrue("# 1", strategy.shouldStartNewSip(object1, null));
    assertTrue("# 2", strategy.shouldStartNewSip(object2, null));
    assertTrue("# 3", strategy.shouldStartNewSip(object3, null));
    assertFalse("Other", strategy.shouldStartNewSip(randomString(), null));
  }

  private SipSegmentationStrategy<String> segmentOn(String segmentObject) {
    return (domainObject, metrics) -> domainObject == segmentObject;
  }

}
