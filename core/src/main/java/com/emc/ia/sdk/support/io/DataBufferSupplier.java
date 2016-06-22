/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

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
      return dataBufferType.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

}
