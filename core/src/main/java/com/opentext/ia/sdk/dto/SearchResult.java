/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;


public class SearchResult extends NamedLinkContainer {

  private int totalElements;
  private int executionTime;
  private boolean empty;
  private List<Row> rows;

  public SearchResult() {
    setRows(new ArrayList<Row>());
  }

  public int getTotalElements() {
    return totalElements;
  }

  public final void setTotalElements(int totalElements) {
    this.totalElements = totalElements;
  }

  public int getExecutionTime() {
    return executionTime;
  }

  public final void setExecutionTime(int executionTime) {
    this.executionTime = executionTime;
  }

  public List<Row> getRows() {
    return rows;
  }

  public final void setRows(List<Row> rows) {
    this.rows = new ArrayList<>(rows.size());
    this.rows.addAll(rows);
  }

  public boolean isEmpty() {
    return empty;
  }

  public void setEmpty(boolean empty) {
    this.empty = empty;
  }

}
