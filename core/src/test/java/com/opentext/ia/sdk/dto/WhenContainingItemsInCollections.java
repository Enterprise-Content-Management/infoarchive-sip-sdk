/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.Assert.assertSame;

import java.util.*;

import org.junit.Test;

import com.opentext.ia.test.TestCase;


public class WhenContainingItemsInCollections extends TestCase {


  @Test
  public void shouldExtractItemsByKey() {
    ItemContainer<NamedLinkContainer> collection = new TestItems();
    String key = collection.getKey();
    Map<String, List<NamedLinkContainer>> embedded = new HashMap<>();
    NamedLinkContainer item1 = new NamedLinkContainer();
    String name1 = randomString();
    item1.setName(name1);
    NamedLinkContainer item2 = new NamedLinkContainer();
    item2.setName(randomString());
    List<NamedLinkContainer> items = Arrays.asList(item1, item2);
    embedded.put(key, items);

    collection.setEmbedded(embedded);

    assertSame("Item by name", item1, collection.byName(name1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfEmbeddedListIsMissing() {
    ItemContainer<NamedLinkContainer> collection = new TestItems();
    collection.setEmbedded(Collections.emptyMap());
  }

  public static class TestItems extends ItemContainer<NamedLinkContainer> {
  }

}
