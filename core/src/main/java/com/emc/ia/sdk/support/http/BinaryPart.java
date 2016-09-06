/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

import java.io.InputStream;

public class BinaryPart extends Part {

  private final InputStream data;
  private final String downloadName;

  public BinaryPart(String name, InputStream data, String downloadName) {
    this(name, MediaTypes.BINARY, data, downloadName);
  }

  public BinaryPart(String name, String mediaType, InputStream data, String downloadName) {
    super(name, mediaType);
    this.data = data;
    this.downloadName = downloadName;
  }

  public InputStream getData() {
    return data;
  }

  public String getDownloadName() {
    return downloadName;
  }

}
