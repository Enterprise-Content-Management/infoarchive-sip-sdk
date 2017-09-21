/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.api;

import java.io.Closeable;
import java.io.InputStream;


/**
 * Result of getting binary content from a SIP.
 */
public interface ContentResult extends Closeable {

  String getFormatMimeType();

  long getLength();

  InputStream getInputStream();

  String getName();

}
