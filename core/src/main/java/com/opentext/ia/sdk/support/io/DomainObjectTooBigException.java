/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Used when a DomainObject is too big to fit in a SIP with the defined limit.
 */
@SuppressFBWarnings(value = "IMC_IMMATURE_CLASS_BAD_SERIALVERSIONUID",
    justification = "backward compatiblity")
public class DomainObjectTooBigException extends RuntimeException {

  private static final long serialVersionUID = -3456477572689104546L;

  private final long size;
  private final long maxSize;

  public DomainObjectTooBigException(long size, long maxSize) {
    this.size = size;
    this.maxSize = maxSize;
  }

  @Override
  public String getMessage() {
    return String.format("DomainObject is %d bytes, but should not be more than %d bytes", size, maxSize);
  }
}
