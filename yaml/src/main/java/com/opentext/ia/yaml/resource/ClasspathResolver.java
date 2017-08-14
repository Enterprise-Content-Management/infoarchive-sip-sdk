/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;


class ClasspathResolver implements ResourceResolver {

  private final Class<?> type;
  private final String path;

  ClasspathResolver() {
    this(ClasspathResolver.class, "/");
  }

  ClasspathResolver(String path) {
    this(ClasspathResolver.class, path + "/");
  }

  /**
   * @since 6.2.0
   */
  ClasspathResolver(Class<?> type) {
    this(type, "");
  }

  /**
   * @since 6.2.0
   */
  ClasspathResolver(Class<?> type, String path) {
    this.type = type;
    this.path = path;
  }

  @Override
  public String apply(String name) {
    try (InputStream input = type.getResourceAsStream(path + name)) {
      if (input == null) {
        throw new UnknownResourceException(name, null);
      }
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UnknownResourceException(name, e);
    }
  }

}
