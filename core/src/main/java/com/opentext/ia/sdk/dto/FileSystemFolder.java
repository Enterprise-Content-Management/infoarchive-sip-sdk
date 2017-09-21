/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class FileSystemFolder extends NamedLinkContainer {

  private String subPath;
  private String parentSpaceRootFolder;

  public String getSubPath() {
    return subPath;
  }

  public void setSubPath(String subPath) {
    this.subPath = subPath;
  }

  public String getParentSpaceRootFolder() {
    return parentSpaceRootFolder;
  }

  public void setParentSpaceRootFolder(String parentSpaceRootFolder) {
    this.parentSpaceRootFolder = parentSpaceRootFolder;
  }

}
