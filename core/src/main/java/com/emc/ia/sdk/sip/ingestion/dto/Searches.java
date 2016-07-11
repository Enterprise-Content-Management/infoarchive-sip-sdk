/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Searches extends ItemContainer<Search> {
  private Page page;

  protected Searches() {
    super("searches");
    setPage(new Page());
  }

  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
  }

}
