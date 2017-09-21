/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;


/**
 * Response from an HTTP server.
 */
public interface Response extends Closeable {

  String getHeaderValue(String name, String defaultValue);

  boolean getHeaderValue(String name, boolean defaultValue);

  int getHeaderValue(String name, int defaultValue);

  InputStream getBody() throws IOException;

}
