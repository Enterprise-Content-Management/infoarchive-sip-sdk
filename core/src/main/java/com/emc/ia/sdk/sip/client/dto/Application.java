/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class Application extends NamedLinkContainer {

  private String type;
  private String archiveType;

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

}
