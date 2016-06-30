/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.ia.sdk.support.rest.LinkContainer;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ItemContainer<T extends NamedLinkContainer> extends LinkContainer {

  private final String key;
  @JsonProperty("_embedded")
  private Map<String, List<T>> itemsByName = new HashMap<>();

  protected ItemContainer(String key) {
    this.key = key;
  }

  public void setItems(Map<String, List<T>> items) {
    this.itemsByName = items;
  }

  public T byName(String name) {
    if (!itemsByName.containsKey(key)) {
      return null;
    }
    return itemsByName.get(key).stream()
        .filter(item -> name.equals(item.getName()))
        .findAny()
        .orElse(null);
  }

}
