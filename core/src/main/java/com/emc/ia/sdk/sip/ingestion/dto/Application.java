/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

public class Application extends NamedLinkContainer {

  private String type = "ACTIVE_ARCHIVING";
  private String archiveType = "SIP";

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getArchiveType() {
    return archiveType;
  }

  public void setArchiveType(String archiveType) {
    this.archiveType = archiveType;
  }

}
