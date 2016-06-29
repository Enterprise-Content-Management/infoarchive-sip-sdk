/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;


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
