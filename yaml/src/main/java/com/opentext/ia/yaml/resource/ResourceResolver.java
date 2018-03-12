/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import java.io.File;
import java.util.function.Function;


/**
 * Resolve a resource name to its contents.
 */
public interface ResourceResolver extends Function<String, String> {

  /**
   * Returns a resolver that can't resolve anything. Use this when you're sure no external resources need resolving.
   * @return a resolver that can't resolve anything
   */
  static ResourceResolver none() {
    return new RejectResourcesResolver();
  }

  /**
   * Returns a resolver that resolves relative files.
   * @param file The file relative to which resource names are resolved
   * @return a resolver that resolves relative files
   */
  static ResourceResolver fromFile(File file) {
    return new FileResolver(file);
  }

  /**
   * Returns a resolver that resolves resources from the root of the classpath.
   * @return a resolver that resolves resources from the root of the classpath
   */
  static ResourceResolver fromClasspath() {
    return fromClasspath("");
  }

  /**
   * Returns a resolver that resolves resources from a given path in the classpath.
   * @param path The path where resources are searched
   * @return a resolver that resolves resources from a given path in the classpath
   */
  static ResourceResolver fromClasspath(String path) {
    return new ClasspathResolver(path);
  }

  /**
   * Returns a resolver that resolves resources from a given path in the classpath.
   * @param type The class in which package resources are searched
   * @return a resolver that resolves resources from the package of a given class in the classpath
   * @since 6.2.0
   */
  static ResourceResolver fromClasspath(Class<?> type) {
    return new ClasspathResolver(type);
  }

}
