/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;


/**
 * Provide repeatable access to the same {@linkplain InputStream} by caching it in memory.
 */
public class RepeatableInputStream implements Supplier<InputStream> {

  private final ByteArrayInputOutputStream provider = new ByteArrayInputOutputStream();

  /**
   * Provide repeatable access to the given input stream.
   * @param source The input stream to make available for repeated access
   * @throws IOException When an I/O error occurs
   */
  public RepeatableInputStream(InputStream source) throws IOException {
    IOUtils.copy(source, provider);
  }

  @Override
  public InputStream get() {
    return provider.getInputStream();
  }

}
