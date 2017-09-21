/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.io;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;


public class WhenHandlingIoExceptions {

  @Test
  public void shouldWrapIntoRuntimeException() {
    IOException original = new IOException();

    RuntimeIoException wrapper = new RuntimeIoException(original);

    assertTrue("Not a RuntimeException", RuntimeException.class.isAssignableFrom(wrapper.getClass()));
    assertSame("Wrapped exception", original, wrapper.getCause());
  }

}
