/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.dto;


public class Application extends NamedLinkContainer {

  private String type;
  private String archiveType;
  private String description;
  private String category;

  public Application() {
    setType("ACTIVE_ARCHIVING");
    setArchiveType("SIP");
  }

  public String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }

  public String getArchiveType() {
    return archiveType;
  }

  public final void setArchiveType(String archiveType) {
    this.archiveType = archiveType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

}
