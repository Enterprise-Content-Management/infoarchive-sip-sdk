/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;


public interface HttpClient {

  <T> T get(String uri, Collection<Header> headers, Class<T> type) throws IOException;

  <T> T get(String uri, Collection<Header> headers, ResponseFactory<T> factory) throws IOException;

  <T> T put(String uri, Collection<Header> headers, Class<T> type) throws IOException;

  <T> T put(String uri, Collection<Header> headers, Class<T> type, String payload) throws IOException;

  <T> T put(String uri, Collection<Header> headers, Class<T> type, InputStream payload) throws IOException;

  <T> T post(String uri, Collection<Header> headers, Class<T> type, String payload) throws IOException;

  <T> T post(String uri, Collection<Header> headers, Class<T> type, InputStream payload) throws IOException;

  @Deprecated
  default <T> T post(String uri, Collection<Header> headers, String payload, Class<T> type) throws IOException {
    return post(uri, headers, type, payload);
  }

  <T> T post(String uri, Collection<Header> headers, Class<T> type, Collection<Part> parts) throws IOException;

  default void delete(String uri, Collection<Header> headers) throws IOException {
    delete(uri, headers, null);
  }

  <T> T delete(String uri, Collection<Header> headers, Class<T> type) throws IOException;

  void close();

  UriBuilder uri(String baseUri);

}
