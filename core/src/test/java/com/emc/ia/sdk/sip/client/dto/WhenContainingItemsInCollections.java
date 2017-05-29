/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;

public class WhenContainingItemsInCollections extends TestCase {

  @Test
  public void shouldExtractItemsByKey() {
    String key = randomString(8);
    ItemContainer<NamedLinkContainer> collection = new ItemContainer<>(key);
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
    String key = randomString(8);

    ItemContainer<NamedLinkContainer> collection = new ItemContainer<>(key);
    collection.setEmbedded(Collections.emptyMap());
  }

}
