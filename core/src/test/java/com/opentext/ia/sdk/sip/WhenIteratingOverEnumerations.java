/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;

import com.opentext.ia.sdk.support.test.TestCase;


public class WhenIteratingOverEnumerations extends TestCase {

  @Test
  public void shouldProvideAllItems() {
    Collection<String> expected = Arrays.asList(randomString(), randomString());

    Collection<String> actual = new ArrayList<>();
    Iterator<String> iterator = new EnumerationIterator<>(Collections.enumeration(expected));
    iterator.forEachRemaining(item -> actual.add(item));

    assertEquals("Items", expected, actual);
  }

}
