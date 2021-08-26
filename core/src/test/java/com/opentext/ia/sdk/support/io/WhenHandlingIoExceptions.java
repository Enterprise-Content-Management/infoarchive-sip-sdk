/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;


public class WhenHandlingIoExceptions {

  @Test
  public void shouldWrapIntoRuntimeException() {
    IOException original = new IOException();

    RuntimeIoException wrapper = new RuntimeIoException(original);

    assertTrue(RuntimeException.class.isAssignableFrom(wrapper.getClass()),
        "Not a RuntimeException");
    assertSame(original, wrapper.getCause(), "Wrapped exception");
  }

}
