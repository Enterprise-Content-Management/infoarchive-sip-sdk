/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;

import com.opentext.ia.sdk.support.JavaBean;


public class Row extends JavaBean {

  private String id;
  private List<Column> columns;

  public Row() {
    setColumns(new ArrayList<Column>());
  }

  public String getId() {
    return id;
  }

  public final void setId(String id) {
    this.id = id;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public final void setColumns(List<Column> columns) {
    this.columns = columns;
  }

}
