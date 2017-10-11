/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.opentext.ia.sdk.support.io.DomainObjectTooBigException;
import com.opentext.ia.test.TestCase;


public class WhenSegmentingDomainObjectsIntoSips extends TestCase {

  private static final String[] CONTENT_OBJECTS = new String[] { "Hello", "Doman", "yuiopqwertyuiop",
      "poiuytrewqpoiuytrewq" };
  private SipSegmentationStrategy<String> strategy;
  private int expected;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

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

  @Test
  public void shouldSegmentByRandomMaxProspectiveSipSize() throws IOException {
    int maxSize = randomInt(50, 150);
    String[] contentObjects = randomStrings(randomInt(4, 8));
    if (sizeOf(contentObjects) > maxSize) {
      expectedException.expect(DomainObjectTooBigException.class);
    }

    assertSegmentByProspectiveSipSize(maxSize, contentObjects);
  }

  private String[] randomStrings(int numStrings) {
    String[] result = new String[numStrings];
    for (int i = 0; i < numStrings; i++) {
      result[i] = randomString(randomInt(5, 20));
    }
    return result;
  }

  private int sizeOf(String[] domainObjects) {
    return Arrays.stream(domainObjects)
        .collect(Collectors.summingInt(String::length));
  }

  private void assertSegmentByProspectiveSipSize(int maxSize, String... contentObjects) throws IOException {
    SipSegmentationStrategy<String[]> byMaxSize = SipSegmentationStrategy.byMaxProspectiveSipSize(maxSize,
        new ContentObjectsToDigitalObjects());
    int size = sizeOf(contentObjects);
    int expectedSips = 0;
    int sipSize = 0;
    int actualSips = 0;
    Counters metrics = new Counters();
    SipMetrics sipMetrics = new SipMetrics(metrics);

    int numDomainObjects = randomInt(6, 10);
    for (int i = 0; i < numDomainObjects; i++) {
      if (sipSize == 0) {
        expectedSips++;
      }
      sipSize += size;
      if (metrics.get(SipMetrics.SIZE_SIP) == 0) {
        actualSips++;
      }
      metrics.inc(SipMetrics.SIZE_SIP, size);

      // Expected behavior
      if (sipSize + size > maxSize) {
        sipSize = 0;
      }

      // Actual behavior
      if (byMaxSize.shouldStartNewSip(contentObjects, sipMetrics)) {
        metrics.set(SipMetrics.SIZE_SIP, 0);
      }
    }
    assertEquals("# SIPs", expectedSips, actualSips);
  }

  @Test
  public void shouldSegmentByMaxProspectiveSipSizeHalfMax() throws IOException {
    assertSegmentByProspectiveSipSize(90, CONTENT_OBJECTS);
  }

  @Test
  public void shouldSegmentByMaxProspectiveSipSizeExact() throws IOException {
    assertSegmentByProspectiveSipSize(45, CONTENT_OBJECTS);
  }


  private class ContentObjectsToDigitalObjects implements DigitalObjectsExtraction<String[]> {

    @Override
    public Iterator<? extends DigitalObject> apply(String[] contentObjects) {
      return Arrays.stream(contentObjects)
          .map(contentObject -> DigitalObject.fromString(randomString(), contentObject, StandardCharsets.UTF_8))
          .collect(Collectors.toList())
          .iterator();
    }

  }

}
