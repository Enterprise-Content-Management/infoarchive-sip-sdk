/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.Assert;

public final class TestUtil {

  private TestUtil() {
    // Utility class
  }

  public static <T> void assertEquals(String message, Collection<T> expected, Collection<T> actual) {
    String details = String.format("%nWanted:%n%s%nGotten:%n%s%n", join(expected), join(actual));
    Iterator<T> wanted = expected.iterator();
    Iterator<T> gotten = actual.iterator();
    while (wanted.hasNext()) {
      T wantedItem = wanted.next();
      Assert.assertTrue(message + " - missing item: " + wantedItem + details, gotten.hasNext());

      T gottenItem = gotten.next();
      if (!Objects.equals(wantedItem, gottenItem)) {
        Assert.assertEquals(message + " - wrong item" + details, String.valueOf(wantedItem), String.valueOf(gottenItem));
      }
    }
    Assert.assertFalse(message + " - extra items" + details, gotten.hasNext());
  }

  private static <T> String join(Collection<T> items) {
    return items.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(System.lineSeparator()));
  }

}
