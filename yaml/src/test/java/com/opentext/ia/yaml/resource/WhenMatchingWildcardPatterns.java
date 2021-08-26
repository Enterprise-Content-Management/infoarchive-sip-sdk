/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;


class WhenMatchingWildcardPatterns extends TestCase {

  @Test
  void shouldMatchLiterals() {
    String pattern = randomString(12);

    assertMatch(pattern, true, pattern);
    assertMatch(pattern, false, randomString(5));
  }

  private void assertMatch(String pattern, boolean expected, String value) {
    Predicate<String> matcher = new MatchesWildcardPattern<>(pattern, Function.identity());
    assertEquals(expected, matcher.test(value), String.format("%s ~ %s", value, pattern));
  }

  @Test
  void shouldMatchQuestionMarkToAnyCharacter() {
    String suffix = randomString(3);
    String pattern = '?' + suffix;

    assertMatch(pattern, true, randomString(1) + suffix);
    assertMatch(pattern, false, randomString(3) + suffix);
    assertMatch(pattern, false, suffix + randomString(1));
  }

  @Test
  void shouldMatchAsteriskToAnyCharactersButPathSeparators() {
    String suffix = randomString(3);
    String pattern = '*' + suffix;

    assertMatch(pattern, true, randomString(randomInt(1, 3)) + suffix);
    assertMatch(pattern, false, randomString(1) + '/' + suffix);
    assertMatch(pattern, false, suffix + randomString(1));
  }

  @Test
  void shouldMatchTwoAsteriskesToAnyCharactersIncludingPathSeparators() {
    String suffix = randomString(3);
    String pattern = "**/*" + suffix;

    assertMatch(pattern, true, randomString(3) + '/' + randomString(2) + '/' + randomString(4) + suffix);
  }

  @Test
  void shouldMatchSpecialChatractersAsIs() {
    String pattern = randomString(2) + "(foo-bar)" + randomString(3);

    assertMatch(pattern, true, pattern);
  }

}
