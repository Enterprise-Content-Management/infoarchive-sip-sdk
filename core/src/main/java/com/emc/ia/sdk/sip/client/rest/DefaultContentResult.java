/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.emc.ia.sdk.sip.client.ContentResult;

public class DefaultContentResult implements ContentResult {

  private final InputStream stream;
  private final long length;
  private final String mimetype;
  private final Closeable dependentResource;
  private final String name;

  public DefaultContentResult(String name, long length, String mimetype, InputStream stream,
      Closeable dependentResource) {
    this.name = name;
    this.length = length;
    this.mimetype = mimetype;
    this.stream = stream;
    this.dependentResource = dependentResource;
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
    IOUtils.closeQuietly(stream);
    IOUtils.closeQuietly(dependentResource);
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
