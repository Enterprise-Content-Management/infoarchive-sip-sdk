/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

public class SearchOptions {

  private int pagesize;
  private int timeoutInSec;
  private String searchSetName;

  public SearchOptions() {
    setPagesize(20);
    setTimeoutInSec(120);
    setSearchSetName("Email");
  }

  public SearchOptions getDefault() {
    return new SearchOptions();
  }

  public int getPagesize() {
    return pagesize;
  }

  public final void setPagesize(int pagesize) {
    this.pagesize = pagesize;
  }

  public int getTimeoutInSec() {
    return timeoutInSec;
  }

  public final void setTimeoutInSec(int timeoutInSec) {
    this.timeoutInSec = timeoutInSec;
  }

  public String getSearchSetName() {
    return searchSetName;
  }

  public final void setSearchSetName(String searchSetName) {
    this.searchSetName = searchSetName;
  }

}
