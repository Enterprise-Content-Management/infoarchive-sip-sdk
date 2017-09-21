/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;


@JsonRootName(value = "data")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SearchQuery {

  private final List<Item> items;

  private final List<OrderBy> orderBys;

  public SearchQuery() {
    this.items = new ArrayList<>();
    this.orderBys = new ArrayList<>();
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    Objects.requireNonNull(items);
    this.items.addAll(items);
  }

  @JsonProperty("order-by")
  public List<OrderBy> getOrderBys() {
    return orderBys;
  }

  public void setOrderBys(List<OrderBy> orderBys) {
    Objects.requireNonNull(orderBys);
    this.orderBys.addAll(orderBys);
  }

}
