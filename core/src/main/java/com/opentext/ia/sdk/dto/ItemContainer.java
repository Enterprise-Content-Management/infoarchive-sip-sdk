/*
 * Copyright (c) OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opentext.ia.sdk.support.http.rest.LinkContainer;


public class ItemContainer<T extends NamedLinkContainer> extends LinkContainer {

  private final String key;
  private final Collection<T> items = new ArrayList<>();
  private PageMetadata metadata;

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

  @JsonProperty("page")
  public PageMetadata getMetadata() {
    return metadata;
  }

  public void setMetadata(PageMetadata metadata) {
    this.metadata = metadata;
  }

  @Override
  public String toString() {
    return String.format("items=%s; %s", items, super.toString());
  }

  public static class PageMetadata {

    @JsonProperty
    private long size;
    @JsonProperty
    private long totalElements;
    @JsonProperty
    private long totalPages;
    @JsonProperty
    private long number;

    protected PageMetadata() {

    }

    public long getSize() {
      return size;
    }

    public void setSize(long size) {
      this.size = size;
    }

    public long getTotalElements() {
      return totalElements;
    }

    public void setTotalElements(long totalElements) {
      this.totalElements = totalElements;
    }

    public long getTotalPages() {
      return totalPages;
    }

    public void setTotalPages(long totalPages) {
      this.totalPages = totalPages;
    }

    public long getNumber() {
      return number;
    }

    public void setNumber(long number) {
      this.number = number;
    }
  }
}
