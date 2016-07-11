/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class SearchComposition extends NamedLinkContainer {
  private String searchName;

  public SearchComposition() {
    setSearchName("Deafult emails Search Compoistion"); //TODO - need to change default value
  }

  public String getSearchName() {
    return searchName;
  }

  public void setSearchName(String searchName) {
    this.searchName = searchName;
  }

}
