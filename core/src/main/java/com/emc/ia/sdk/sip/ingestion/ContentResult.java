/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class ContentResult implements Closeable {

  private final InputStream stream;
  private final long length;
  private final String mimetype;
  private final Closeable dependentResource;
  private final String name;

  public ContentResult(String name, long length, String mimetype, InputStream stream, Closeable dependentResource) {
    this.name = name;
    this.length = length;
    this.mimetype = mimetype;
    this.stream = stream;
    this.dependentResource = dependentResource;
  }

  public String getFormatMimeType() {
    return mimetype;
  }

  public long getLength() {
    return length;
  }

  @Override
  public void close() throws IOException {
    IOUtils.closeQuietly(stream);
    IOUtils.closeQuietly(dependentResource);
  }

  public InputStream getInputStream() {
    return stream;
  }

  public String getName() {
    return name;
  }

}
