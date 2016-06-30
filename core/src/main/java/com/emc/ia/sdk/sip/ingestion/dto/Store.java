/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Store extends NamedLinkContainer {

  private String fileSystemFolder;
  private String type;
  private String status;

  public Store() {
    setType("FILESYSTEM");
    setStatus("ONLINE");
  }

  public String getFileSystemFolder() {
    return fileSystemFolder;
  }

  public final void setFileSystemFolder(String fileSystemFolder) {
    this.fileSystemFolder = fileSystemFolder;
  }

  public String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public final void setStatus(String status) {
    this.status = status;
  }

}
