/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class OrderItem extends NamedLinkContainer {

  private String type;

  public OrderItem() {
    setType("EXPORT");
  }

  public String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }

}
