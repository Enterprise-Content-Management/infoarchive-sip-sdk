/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;


/**
 * Resource resolver that doesn't actually resolve anything, bit always throws an exception.
 */
class RejectResourcesResolver implements ResourceResolver {

  @Override
  public String apply(String name) {
    throw new UnknownResourceException(name, null);
  }

}
