/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class FileSystemRoots extends ItemContainer<FileSystemRoot> {

  public FileSystemRoots() {
    super("fileSystemRoots");
  }

  public FileSystemRoot first() {
    return getItems()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No FileSystemRoot defined"));
  }

}
