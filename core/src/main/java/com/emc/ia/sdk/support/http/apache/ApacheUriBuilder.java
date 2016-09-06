/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http.apache;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

import com.emc.ia.sdk.support.http.UriBuilder;

public class ApacheUriBuilder implements UriBuilder {

  private final URIBuilder builder;

  public ApacheUriBuilder(String baseUri) {
    try {
      builder = new URIBuilder(baseUri);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public UriBuilder addParameter(String name, String value) {
    builder.addParameter(name, value);
    return this;
  }

  @Override
  public String build() {
    try {
      return builder.build()
        .toString();
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

}
