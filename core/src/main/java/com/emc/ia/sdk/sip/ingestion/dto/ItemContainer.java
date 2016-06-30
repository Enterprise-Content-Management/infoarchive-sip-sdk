/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.emc.ia.sdk.support.rest.LinkContainer;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ItemContainer<T extends NamedLinkContainer> extends LinkContainer {

  private final String key;
  private final Collection<T> items = new ArrayList<>();

  protected ItemContainer(String key) {
    this.key = key;
  }

  @JsonProperty("_embedded")
  protected void setEmbedded(Map<String, List<T>> embedded) {
    items.clear();
    List<T> embeddedItems = embedded.get(key);
    if (embeddedItems == null) {
      throw new IllegalArgumentException(
          String.format("Expected items under key '%s', but got keys %s", key, embedded.keySet()));
    }
    items.addAll(embeddedItems);
  }

  public T byName(String name) {
    return getItems()
        .filter(item -> name.equals(item.getName()))
        .findAny()
        .orElse(null);
  }

  protected Stream<T> getItems() {
    return items.stream();
  }

  @Override
  public String toString() {
    return String.format("items=%s; %s", items, super.toString());
  }

}
