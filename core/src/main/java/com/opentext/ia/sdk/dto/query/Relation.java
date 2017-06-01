/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.query;

import java.util.ArrayList;
import java.util.List;


public class Relation implements Item {

  private final List<Item> items;

  protected Relation() {
    items = new ArrayList<>();
  }

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items.clear();
    this.items.addAll(items);
  }

}
