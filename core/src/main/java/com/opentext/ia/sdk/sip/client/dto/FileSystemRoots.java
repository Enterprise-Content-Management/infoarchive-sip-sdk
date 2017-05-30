/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

public class FileSystemRoots extends ItemContainer<FileSystemRoot> {

  public FileSystemRoots() {
    super("fileSystemRoots");
  }

  public FileSystemRoot first() {
    return getItems().findFirst()
      .orElseThrow(() -> new IllegalStateException("No FileSystemRoot defined"));
  }

}
