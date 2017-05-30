/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

public class Store extends NamedLinkContainer {

  private String fileSystemFolder;
  private String type;
  private String status;
  private String storeType;

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

  public String getStoreType() {
    return storeType;
  }

  public void setStoreType(String storeType) {
    this.storeType = storeType;
  }

}
