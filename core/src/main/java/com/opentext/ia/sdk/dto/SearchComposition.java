/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class SearchComposition extends VersionedLinkContainer {

  private String searchName;

  public SearchComposition() {
    setName("Set 1");
  }

  public String getSearchName() {
    return searchName;
  }

  public void setSearchName(String searchName) {
    this.searchName = searchName;
  }

}
