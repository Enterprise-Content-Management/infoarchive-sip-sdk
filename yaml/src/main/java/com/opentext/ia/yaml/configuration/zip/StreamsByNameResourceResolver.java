/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.opentext.ia.yaml.resource.MatchesWildcardPattern;
import com.opentext.ia.yaml.resource.ResourceResolver;
import com.opentext.ia.yaml.resource.ResourcesResolver;
import com.opentext.ia.yaml.resource.UnknownResourceException;


class StreamsByNameResourceResolver implements ResourceResolver, ResourcesResolver {

  private final Map<String, InputStream> streamsByName;

  StreamsByNameResourceResolver(Map<String, InputStream> streamsByName) {
    this.streamsByName = streamsByName;
  }

  @Override
  public String apply(String name) {
    if (!streamsByName.containsKey(name)) {
      throw new UnknownResourceException("Missing " + name, null);
    }
    try (InputStream input = streamsByName.get(name)) {
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error reading " + name, e);
    }
  }

  @Override
  public List<String> resolve(String pattern) {
    return streamsByName.keySet().stream()
        .filter(new MatchesWildcardPattern<>(pattern, Function.identity()))
        .map(this)
        .collect(Collectors.toList());
  }

}
