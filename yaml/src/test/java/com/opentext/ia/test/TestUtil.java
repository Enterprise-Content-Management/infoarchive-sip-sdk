/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

public final class TestUtil {

  private TestUtil() {
    // Utility class
  }

  public static <T> void assertEquals(Collection<T> expected, Collection<T> actual,
      String message) {
    String details = String.format("%nWanted:%n%s%nGotten:%n%s%n", join(expected), join(actual));
    Iterator<T> wanted = expected.iterator();
    Iterator<T> gotten = actual.iterator();
    while (wanted.hasNext()) {
      T wantedItem = wanted.next();
      assertTrue(gotten.hasNext(), message + " - missing item: " + wantedItem + details);

      T gottenItem = gotten.next();
      if (!Objects.equals(wantedItem, gottenItem)) {
        Assertions.assertEquals(String.valueOf(wantedItem), String.valueOf(gottenItem),
            message + " - wrong item" + details);
      }
    }
    assertFalse(gotten.hasNext(), message + " - extra items" + details);
  }

  private static <T> String join(Collection<T> items) {
    return items.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(System.lineSeparator()));
  }

}
