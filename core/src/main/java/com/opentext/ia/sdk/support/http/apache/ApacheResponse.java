/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.apache;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;

import com.opentext.ia.sdk.support.http.Response;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation of {@linkplain Response} using the <a href="https://hc.apache.org/">Apache HttpComponents</a>
 * library.
 */
public class ApacheResponse implements Response {

  private final ClassicHttpResponse wrapped;

  @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
          justification = "ApacheResponse is a thin adapter over ClassicHttpResponse and intentionally retains the wrapped response.")
  public ApacheResponse(ClassicHttpResponse wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public void close() throws IOException {
    wrapped.close();
  }

  @Override
  public String getHeaderValue(String name, String defaultValue) {
    Header header = wrapped.getFirstHeader(name);
    return header == null ? defaultValue : header.getValue();
  }

  @Override
  public boolean getHeaderValue(String name, boolean defaultValue) {
    return Boolean.parseBoolean(getHeaderValue(name, Boolean.toString(defaultValue)));
  }

  @Override
  public int getHeaderValue(String name, int defaultValue) {
    return Integer.parseInt(getHeaderValue(name, Integer.toString(defaultValue)));
  }

  @Override
  public InputStream getBody() throws IOException {
    HttpEntity entity = wrapped.getEntity();
    return entity == null ? null : entity.getContent();
  }

}
