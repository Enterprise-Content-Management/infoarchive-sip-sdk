/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * Predicate that tests objects against a wildcard pattern.
 * @param <T> The type of objects to test
 * @since 9.14.0
 */
public class MatchesWildcardPattern<T> implements Predicate<T> {

  private final Function<T, String> objectToPath;
  private final Pattern pathPattern;

  public MatchesWildcardPattern(String wildcardPattern, Function<T, String> objectToPath) {
    this.objectToPath = objectToPath;
    this.pathPattern = Pattern.compile(toPathRegex(wildcardPattern));
  }

  private String toPathRegex(String pattern) {
    return pattern
        .replace(".", "\\.")
        .replace("?", "[^/]")
        .replace("**/", "([^/]+/)+")
        .replace("*", "[^/]*");
  }

  @Override
  public boolean test(T candidate) {
    String path = objectToPath.apply(candidate);
    return pathPattern.matcher(path).matches();
  }

}
