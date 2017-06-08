/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;


public class ClasspathResolver implements ResourceResolver {

  private final String prefix;

  public ClasspathResolver() {
    this("");
  }

  public ClasspathResolver(String prefix) {
    this.prefix = prefix + '/';
  }

  @Override
  public String apply(String name) {
    try (InputStream input = getClass().getResourceAsStream(prefix + name)) {
      if (input == null) {
        throw new UnknownResourceException(name, null);
      }
      return IOUtils.toString(input);
    } catch (IOException e) {
      throw new UnknownResourceException(name, e);
    }
  }

}
