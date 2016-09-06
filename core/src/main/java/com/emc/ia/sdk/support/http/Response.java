/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public interface Response extends Closeable {

  String getHeaderValue(String name, String defaultValue);

  boolean getHeaderValue(String name, boolean defaultValue);

  int getHeaderValue(String name, int defaultValue);

  InputStream getBody() throws IOException;

}
