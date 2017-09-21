/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.impl;

import java.io.IOException;
import java.io.InputStream;

import com.opentext.ia.sdk.client.api.ContentResult;


/**
 * Default implementation of {@linkplain ContentResult}.
 */
public class DefaultContentResult implements ContentResult {

  private final InputStream stream;
  private final long length;
  private final String mimetype;
  private final String name;
  private final Runnable closer;

  public DefaultContentResult(String name, long length, String mimetype, InputStream stream,
      Runnable closer) {
    this.name = name;
    this.length = length;
    this.mimetype = mimetype;
    this.stream = stream;
    this.closer = closer;
  }

  @Override
  public String getFormatMimeType() {
    return mimetype;
  }

  @Override
  public long getLength() {
    return length;
  }

  @Override
  public void close() throws IOException {
    closer.run();
  }

  @Override
  public InputStream getInputStream() {
    return stream;
  }

  @Override
  public String getName() {
    return name;
  }

}
