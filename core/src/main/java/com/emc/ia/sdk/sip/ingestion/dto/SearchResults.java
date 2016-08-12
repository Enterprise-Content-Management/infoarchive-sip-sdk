/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchResults {

  private List<Row> rows;

  public SearchResults() {
    setRows(new ArrayList<Row>());
  }

  public List<Row> getRows() {
    return rows;
  }

  public final void setRows(List<Row> rows) {
    this.rows = rows;
  }

}
