/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;


class WhenContainingItemsInCollections extends TestCase {

  @Test
  void shouldExtractItemsByKey() {
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

    assertSame(item1, collection.byName(name1), "Item by name");
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfEmbeddedListIsMissing() {
    ItemContainer<NamedLinkContainer> collection = new TestItems();
    assertThrows(IllegalArgumentException.class,
        () -> collection.setEmbedded(Collections.emptyMap()));
  }


  public static class TestItems extends ItemContainer<NamedLinkContainer> {
  }

}
