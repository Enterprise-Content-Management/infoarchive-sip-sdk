/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.assembly;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Adapt an {@linkplain Enumeration} to an {@linkplain Iterator}.
 * @param <T> The type of items to enumerate.
 */
public class EnumerationIterator<T> implements Iterator<T> {

  private final Enumeration<T> enumeration;

  public EnumerationIterator(Enumeration<T> enumeration) {
    this.enumeration = enumeration;
  }

  @Override
  public boolean hasNext() {
    return enumeration.hasMoreElements();
  }

  @Override
  public T next() {
    return enumeration.nextElement();
  }

}
