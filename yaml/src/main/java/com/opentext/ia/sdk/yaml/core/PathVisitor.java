/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.core;

import java.util.Collection;
import java.util.stream.Stream;


public abstract class PathVisitor implements Visitor {

  private final Collection<String> pathRegexes;

  public PathVisitor(Collection<String> pathRegexes) {
    this.pathRegexes = pathRegexes;
  }

  @Override
  public boolean test(Visit visit) {
    return pathRegexesMatching(visit)
        .findAny()
        .isPresent();
  }

  protected Stream<String> pathRegexesMatching(Visit visit) {
    return pathRegexes.stream()
        .filter(regex -> visit.getPath().matches('^' + regex + '$'));
  }

}
