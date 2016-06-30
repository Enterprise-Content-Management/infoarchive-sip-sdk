/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class SpaceRootFolder extends NamedLinkContainer {

  private String fileSystemRoot;

  public String getFileSystemRoot() {
    return fileSystemRoot;
  }

  public void setFileSystemRoot(String fileSystemRoot) {
    this.fileSystemRoot = fileSystemRoot;
  }

}
