/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class SearchCompositions extends ItemContainer<SearchComposition> {
  private Page page;

  protected SearchCompositions() {
    super("searchCompositions");
    setPage(new Page());
  }

  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
  }

}
