/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;

/**
 * Provide repeatable access to the same {@linkplain InputStream} by caching it in memory.
 */
public class RepeatableInputStream implements Supplier<InputStream> {

  private final ByteArrayInputOutputStream provider = new ByteArrayInputOutputStream();

  /**
   * Provide repeatable access to the given input stream.
   * @param source The input stream to make available for repeated access. Must not be <code>null</code>
   * @throws IOException When an I/O error occurs
   */
  public RepeatableInputStream(InputStream source) throws IOException {
    IOUtils.copy(Objects.requireNonNull(source), provider);
  }

  @Override
  public InputStream get() {
    return provider.getInputStream();
  }

}
