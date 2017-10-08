/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Arrays;
import java.util.Iterator;

import com.opentext.ia.sdk.support.io.DomainObjectTooBigException;

/**
 * Strategy for segmenting domain objects into different SIPs.
 * @param <D> The type of domain objects to segment into different SIPs
 */
@FunctionalInterface
public interface SipSegmentationStrategy<D> {

  /**
   * Determine whether to start a new SIP.
   * @param domainObject The domain object to be added to either the current SIP or a new one
   * @param metrics Metrics about the assembly of the current SIP
   * @return <code>true</code> if a new SIP should be started for the given domain object, or <code>false</code> if
   *         the domain object should be stored in the current SIP
   */
  boolean shouldStartNewSip(D domainObject, SipMetrics metrics);

  /**
   * Return a {@linkplain SipSegmentationStrategy} that allows a maximum number of AIUs per SIP.
   * @param <D> The type of domain objects to segment into different SIPs
   * @param maxAiusPerSip The maximum number of AIUs that may go into a SIP
   * @return A {@linkplain SipSegmentationStrategy} that allows a maximum number of AIUs per SIP
   */
  static <D> SipSegmentationStrategy<D> byMaxAius(long maxAiusPerSip) {
    return (domainObject, metrics) -> metrics.numAius() % maxAiusPerSip == 0;
  }

  /**
   * Return a {@linkplain SipSegmentationStrategy} that allows a maximum number of digital objects per SIP.
   * @param <D> The type of domain objects to segment into different SIPs
   * @param maxDigitalObjects The maximum number of AIUs that may go into a SIP
   * @return A {@linkplain SipSegmentationStrategy} that allows a maximum number of digital objects per SIP
   */
  static <D> SipSegmentationStrategy<D> byMaxDigitalObjects(long maxDigitalObjects) {
    return (domainObject, metrics) -> metrics.numDigitalObjects() % maxDigitalObjects == 0;
  }

  /**
   * Return a {@linkplain SipSegmentationStrategy} that allows a maximum size (uncompressed) of the PDI per SIP.
   * @param <D> The type of domain objects to segment into different SIPs
   * @param maxSize The maximum size of the PDI
   * @return A {@linkplain SipSegmentationStrategy} that allows a maximum size of the PDI per SIP
   */
  static <D> SipSegmentationStrategy<D> byMaxPdiSize(long maxSize) {
    return (domainObject, metrics) -> metrics.pdiSize() >= maxSize;
  }

  /**
   * Return a {@linkplain SipSegmentationStrategy} that allows a maximum total size (uncompressed) of digital objects
   * per SIP.
   * @param <D> The type of domain objects to segment into different SIPs
   * @param maxSize The maximum size of the digital objects
   * @return A {@linkplain SipSegmentationStrategy} that allows a maximum total size of digital objects per SIP
   */
  static <D> SipSegmentationStrategy<D> byMaxDigitalObjectsSize(long maxSize) {
    return (domainObject, metrics) -> metrics.digitalObjectsSize() >= maxSize;
  }

  /**
   * Return a {@linkplain SipSegmentationStrategy} that allows a maximum total size (uncompressed) of the SIP. Does not
   * take into account the size of the object currently being added to the SIP
   * @param <D> The type of domain objects to segment into different SIPs
   * @param maxSize The maximum size of the SIP
   * @return A {@linkplain SipSegmentationStrategy} that allows a maximum total size of the SIP
   */
  static <D> SipSegmentationStrategy<D> byMaxSipSize(long maxSize) {
    return (domainObject, metrics) -> metrics.sipSize() >= maxSize;
  }

  /**
   * Return a {@linkplain SipSegmentationStrategy} that allows a maximum total size (uncompressed) of the SIP including
   * the object being added.
   * @param <D> The type of domain objects to segment into different SIPs
   * @param maxSize The maximum size of the SIP
   * @param digitalObjectsExtraction The extractor for the type of digital object being added.
   * @return A {@linkplain SipSegmentationStrategy} that allows a maximum total size of the SIP including the object
   *         being added to the SIP
   */
  static <D> SipSegmentationStrategy<D> byMaxProspectiveSipSize(long maxSize,
      DigitalObjectsExtraction<D> digitalObjectsExtraction) {
    return (domainObject,
        metrics) -> metrics.sipSize()
            + getDomainObjectSize(digitalObjectsExtraction.apply(domainObject), maxSize) > maxSize;
  }

  /**
   * Return the size of a domain object in bytes.
   * @param iterator used to iterate over the digital objects contained in the domain object
   * @return the size of the domain object in bytes
   * @throws IOException This is thrown if the DomainObject is larger than the maximum allowed SIP size
   */
  static long getDomainObjectSize(Iterator<? extends DigitalObject> iterator, long maxSize) {
    // TODO if we're not adding the content we shouldn't unpack it
    long size = 0;
    while (iterator.hasNext()) {
      DigitalObject digitalObject = iterator.next();
      size += digitalObject.getSize();
    }
    if (size > maxSize) {
      throw new DomainObjectTooBigException(size, maxSize);
    }
    return size;
  }

  /**
   * Return a {@linkplain SipSegmentationStrategy} that combines a number of partial strategies.
   * @param <D> The type of domain objects to segment into different SIPs
   * @param partialStrategies The partial strategies to combine
   * @return A {@linkplain SipSegmentationStrategy} that combines a number of partial strategies
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <D> SipSegmentationStrategy<D> combining(SipSegmentationStrategy<D>... partialStrategies) {
    return (domainObject, metrics) -> Arrays.stream(partialStrategies)
      .anyMatch(s -> s.shouldStartNewSip(domainObject, metrics));
  }

}
