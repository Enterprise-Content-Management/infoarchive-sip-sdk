/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

public class Column {

  private String name;
  private String value;

  public String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public final void setValue(String value) {
    this.value = value;
  }
}
