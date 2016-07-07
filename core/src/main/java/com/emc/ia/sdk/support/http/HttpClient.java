/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

import java.io.IOException;
import java.util.Collection;


public interface HttpClient {

  <T> T get(String uri, Collection<Header> headers, Class<T> type) throws IOException;

  <T> T get(String uri, Collection<Header> headers, ResponseFactory<T> factory) throws IOException;

  <T> T put(String uri, Collection<Header> headers, Class<T> type) throws IOException;

  <T> T post(String uri, Collection<Header> headers, String payload, Class<T> type) throws IOException;

  <T> T post(String uri, Collection<Header> headers, Class<T> type, Part... parts) throws IOException;

  void close();

}
