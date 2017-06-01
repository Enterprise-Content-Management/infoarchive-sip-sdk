/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.dto;

import java.util.*;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opentext.ia.sdk.support.http.rest.LinkContainer;


public class ItemContainer<T extends NamedLinkContainer> extends LinkContainer {

  private final String key;
  private final Collection<T> items = new ArrayList<>();

  protected ItemContainer() {
    this.key = initLower(getClass().getSimpleName());
  }

  private String initLower(String value) {
    return Character.toLowerCase(value.charAt(0)) + value.substring(1);
  }

  protected String getKey() {
    return key;
  }

  @JsonProperty("_embedded")
  protected void setEmbedded(Map<String, List<T>> embedded) {
    items.clear();
    List<T> embeddedItems = embedded.get(key);
    if (embeddedItems == null) {
      throw new IllegalArgumentException(String.format("Expected items under key '%s', but got keys %s", key,
          embedded.keySet()));
    }
    items.addAll(embeddedItems);
  }

  public T byName(String name) {
    Objects.requireNonNull(name, "Missing name");
    return getItems()
        .filter(item -> name.equals(item.getName()))
        .findAny()
        .orElse(null);
  }

  public Stream<T> getItems() {
    return items.stream();
  }

  public boolean hasItems() {
    return getItems().findAny().isPresent();
  }

  public T first() {
    return getItems().findFirst().orElseThrow(() -> new IllegalStateException("No " + key + " defined"));
  }

  @Override
  public String toString() {
    return String.format("items=%s; %s", items, super.toString());
  }

}
