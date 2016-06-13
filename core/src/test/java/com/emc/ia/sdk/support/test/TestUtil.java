/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.test;

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
    String details = "\nWanted:\n" + join(expected) + "\nGotten:\n" + join(actual) + '\n';
    Iterator<T> wanted = expected.iterator();
    Iterator<T> gotten = actual.iterator();
    while (wanted.hasNext()) {
      T wantedItem = wanted.next();
      Assert.assertTrue("Missing item: " + wantedItem + details, gotten.hasNext());

      T gottenItem = gotten.next();
      if (!Objects.equals(wantedItem, gottenItem)) {
        Assert.assertEquals("Wrong item" + details, String.valueOf(wantedItem), String.valueOf(gottenItem));
      }
    }
    Assert.assertFalse("Extra items" + details, gotten.hasNext());
  }

  private static <T> String join(Collection<T> items) {
    return items.stream().map(i -> String.valueOf(i)).collect(Collectors.joining("\n"));
  }

}
