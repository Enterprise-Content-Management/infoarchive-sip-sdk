/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client;

import java.io.Closeable;
import java.io.InputStream;

public interface ContentResult extends Closeable {

  String getFormatMimeType();

  long getLength();

  InputStream getInputStream();

  String getName();
}
