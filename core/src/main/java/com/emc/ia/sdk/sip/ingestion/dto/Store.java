/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Store extends NamedLinkContainer {

  private String fileSystemFolder;
  private String type = "FILESYSTEM";
  private String status = "ONLINE";

  public String getFileSystemFolder() {
    return fileSystemFolder;
  }

  public void setFileSystemFolder(String fileSystemFolder) {
    this.fileSystemFolder = fileSystemFolder;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
