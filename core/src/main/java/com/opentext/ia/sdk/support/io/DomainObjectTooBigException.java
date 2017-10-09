/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

/**
 * Extends {@linkplain RuntimeException}. Used when a DomainObject is too big to fit in a SIP with the defined limit.
 */
public class DomainObjectTooBigException extends RuntimeException {

  private static final long serialVersionUID = -3456477572689104546L;

  private final long domainObjectSize;
  private final long maxSipSize;

  public DomainObjectTooBigException(long inDomainObjectSize, long inMaxSipSize) {
    domainObjectSize = inDomainObjectSize;
    maxSipSize = inMaxSipSize;
  }

  @Override
  public String getMessage() {
    return "DomainObject is " + domainObjectSize + " bytes, but SegmentationStrategy MaxSize is set to " + maxSipSize
        + " bytes.";
  }
}
