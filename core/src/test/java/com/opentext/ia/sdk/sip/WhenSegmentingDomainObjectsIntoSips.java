/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.opentext.ia.sdk.support.io.DomainObjectTooBigException;
import com.opentext.ia.test.TestCase;


public class WhenSegmentingDomainObjectsIntoSips extends TestCase {

  private SipSegmentationStrategy<String> strategy;
  private int expected;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

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
  public void shouldSegmentByProspectiveSipSize() throws IOException {
    int noSips;
    // Random Strings
    noSips = executeSegmentByProspectiveSipSize(randomInt(50, 150), randomArrayOfStrings(randomInt(4, 8)));
    assertEquals("SIP counts - Random generated Strings", expected, noSips);

    // Boundary Test - 45 size and 90 limit
    noSips = executeSegmentByProspectiveSipSize(90,
        new String[] { "Hello", "Doman", "yuiopqwertyuiop", "poiuytrewqpoiuytrewq" });
    assertEquals("SIP counts - Fixed Length Strings", expected, noSips);

    // Boundary Test - 1 sip per AIU
    noSips = executeSegmentByProspectiveSipSize(45,
        new String[] { "Hello", "Doman", "yuiopqwertyuiop", "poiuytrewqpoiuytrewq" });
    assertEquals("SIP counts - Fixed Length Strings", expected, noSips);
  }

  @Test
  public void shouldThrowDomainObjectTooBigException() throws IOException {
    thrown.expect(DomainObjectTooBigException.class);
    executeSegmentByProspectiveSipSize(44,
        new String[] { "Hello", "Doman", "yuiopqwertyuiop", "poiuytrewqpoiuytrewq" });
  }

  private int executeSegmentByProspectiveSipSize(int sipSizeLimit, String[] stringArray) throws IOException {
    // Domain Object to test with
    class TestDomainObject {

      private String[] containedStrings;

      TestDomainObject(String[] inContainedStrings) {
        containedStrings = inContainedStrings;
      }
    }

    // DigitalObjectsExtraction needs to be created and given to the segmentation strategy
    class TestDomainObjectToDigitalObjects implements DigitalObjectsExtraction<TestDomainObject> {

      @Override
      public Iterator<? extends DigitalObject> apply(TestDomainObject testDomainObject) {
        final ArrayList<DigitalObject> digiObjs = new ArrayList<>();
        ArrayList<String> myStrings = new ArrayList<>(Arrays.asList(testDomainObject.containedStrings));
        myStrings.forEach(eachWord -> digiObjs.add(DigitalObject.fromBytes(randomString(), eachWord.getBytes())));
        return digiObjs.iterator();
      }
    }

    int domainObjectSize = 0;
    for (String thisOne : stringArray) {
      domainObjectSize += thisOne.length();
    }
    Counters metrics = new Counters();

    SipSegmentationStrategy<TestDomainObject> localStrategy =
        SipSegmentationStrategy.byMaxProspectiveSipSize(sipSizeLimit, new TestDomainObjectToDigitalObjects());

    // Variables to help determine what to expect from the test
    int noDomainObjects = randomInt(6, 10);
    int sipSizeSoFar = 0;
    int expectedNumberOfSIPs = 1;
    int actualNumberOfSIPs = 1;

    for (int i = 0; i < noDomainObjects; i++) {
      sipSizeSoFar += domainObjectSize;
      metrics.inc(SipMetrics.SIZE_SIP, domainObjectSize);

      TestDomainObject testDomainObject = new TestDomainObject(stringArray);
      // Here we test the MaxProspectiveSipSize SegmentationStrategy
      if (localStrategy.shouldStartNewSip(testDomainObject, new SipMetrics(metrics))) {
        actualNumberOfSIPs++;
        metrics.set(SipMetrics.SIZE_SIP, 0);
      }
      if (sipSizeSoFar + domainObjectSize > sipSizeLimit) {
        expectedNumberOfSIPs++;
        sipSizeSoFar = 0;
      }
    }
    // If the SIP size is 0 after the last SIP is started, then the last SIP is empty so we take it off the total
    if (sipSizeSoFar == 0) {
      expectedNumberOfSIPs--;
    }
    if (metrics.get(SipMetrics.SIZE_SIP) == 0) {
      actualNumberOfSIPs--;
    }
    expected = expectedNumberOfSIPs;
    return actualNumberOfSIPs;
  }

  private String[] randomArrayOfStrings(int numberOfStrings) {
    String[] arrayOfStrings = new String[numberOfStrings];
    for (int i = 0; i < numberOfStrings; i++) {
      arrayOfStrings[i] = randomString(randomInt(5, 20));
    }
    return arrayOfStrings;
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
