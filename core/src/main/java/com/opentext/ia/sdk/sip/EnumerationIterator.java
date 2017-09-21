/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
    if (!enumeration.hasMoreElements()) {
      throw new NoSuchElementException();
    }
    return enumeration.nextElement();
  }

}
