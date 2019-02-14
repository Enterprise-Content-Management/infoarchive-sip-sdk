/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;


/**
 * Supply instances of a particular type of {@linkplain DataBuffer}.
 */
public class DataBufferSupplier<T extends DataBuffer> implements Supplier<T> {

  private final Class<T> dataBufferType;

  /**
   * Create a supplier of {@linkplain DataBuffer}s of a given type.
   * @param dataBufferType The type of data buffer to supply
   */
  public DataBufferSupplier(Class<T> dataBufferType) {
    this.dataBufferType = dataBufferType;
  }

  @Override
  public T get() {
    try {
      return dataBufferType.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException("Failed to instantiate " + dataBufferType.getName(), e);
    }
  }

}
