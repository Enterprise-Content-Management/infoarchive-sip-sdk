/*
 * Copyright (c) OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.time.OffsetDateTime;

import com.opentext.ia.sdk.support.http.rest.LinkContainer;


public class NamedLinkContainer extends LinkContainer {

  private String name;
  private String lastModifiedBy;
  private OffsetDateTime lastModifiedDate;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public OffsetDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

}
