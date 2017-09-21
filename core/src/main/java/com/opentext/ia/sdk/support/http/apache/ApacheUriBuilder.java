/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.apache;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import com.opentext.ia.sdk.support.http.UriBuilder;


/**
 * Implementation of {@linkplain UriBuilder} using the <a href="https://hc.apache.org/">Apache HttpComponents</a>
 * library.
 */
public class ApacheUriBuilder implements UriBuilder {

  private final URIBuilder builder;
  private final Map<String, Object> parameters = new HashMap<>();

  public ApacheUriBuilder(String baseUri) {
    URI base = URI.create(baseUri);
    String query = base.getQuery();
    if (StringUtils.isNotBlank(query)) {
      String uriWithoutQuery = base.toString();
      uriWithoutQuery = uriWithoutQuery.substring(0, uriWithoutQuery.length() - query.length() - 1);
      base = URI.create(uriWithoutQuery);
    }
    builder = new URIBuilder(base);
    if (StringUtils.isNotBlank(query)) {
      for (String nameAndValue : query.split("&")) {
        String[] nameValue = nameAndValue.split("=");
        parameters.put(nameValue[0], nameValue[1]);
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public UriBuilder addParameter(String name, String value) {
    Object prev = parameters.get(name);
    if (!(prev instanceof List)) {
      prev = new ArrayList<>();
    }
    ((List<String>)prev).add(value);
    parameters.put(name, prev);
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public String build() {
    parameters.forEach((name, value) -> {
      if (value instanceof List) {
        ((List<String>)value).forEach(v ->
          builder.addParameter(name, v));
      } else {
        builder.addParameter(name, (String)value);
      }
    });
    try {
      return builder.build().toString();
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

}
