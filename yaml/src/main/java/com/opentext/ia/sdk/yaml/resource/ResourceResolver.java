/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.resource;

import java.io.File;
import java.util.function.Function;


/**
 * Resolve a resource name to its contents.
 */
public interface ResourceResolver extends Function<String, String> {

  static ResourceResolver none() {
    return name -> {
      throw new UnknownResourceException(name, null);
    };
  }

  static ResourceResolver fromFile(File file) {
    return new FileResolver(file);
  }

  static ResourceResolver fromClasspath() {
    return fromClasspath("");
  }

  static ResourceResolver fromClasspath(String prefix) {
    return new ClasspathResolver(prefix);
  }

}
