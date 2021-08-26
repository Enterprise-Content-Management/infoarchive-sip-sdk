/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;


public class WhenIteratingOverEnumerations extends TestCase {

  @Test
  public void shouldProvideAllItems() {
    Collection<String> expected = Arrays.asList(randomString(), randomString());

    Collection<String> actual = new ArrayList<>();
    Iterator<String> iterator = new EnumerationIterator<>(Collections.enumeration(expected));
    iterator.forEachRemaining(item -> actual.add(item));

    assertEquals(expected, actual, "Items");
  }

}
