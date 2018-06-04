/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import java.util.Collections;
import java.util.List;


/**
 * Resolve a resource pattern to the contents of potentially multiple resources.
 * @since 9.14.0
 */
@FunctionalInterface
public interface ResourcesResolver {

  /**
   * Find all resources that match a given pattern and return their contents.
   * @param pattern The pattern to match. Different implementations may support different types of pattern matching,
   * or even none at all
   * @return The contents of all matching resources
   */
  List<String> resolve(String pattern);

  /**
   * Returns a resolver that doesn't supports patterns.
   * @param resourceResolver A resolver for single resources
   * @return a resolver that doesn't supports patterns
   */
  static ResourcesResolver basedOn(ResourceResolver resourceResolver) {
    return pattern -> Collections.singletonList(resourceResolver.apply(pattern));
  }

}
