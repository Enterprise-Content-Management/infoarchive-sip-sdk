/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.ArrayList;
import java.util.List;

public class Row {

  private List<Column> columns;

  public Row() {
    setColumns(new ArrayList<Column>());
  }

  public List<Column> getColumns() {
    return columns;
  }

  public final void setColumns(List<Column> columns) {
    this.columns = columns;
  }

}
