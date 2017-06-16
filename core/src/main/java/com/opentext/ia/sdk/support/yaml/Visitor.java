/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.yaml;

import java.util.function.Consumer;
import java.util.function.Predicate;


public interface Visitor extends Consumer<Visit>, Predicate<Visit> {

  @Override
  default boolean test(Visit visit) {
    return true;
  }

  default int maxNesting() {
    return Integer.MAX_VALUE;
  }

}
