/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class XdbLibrary extends NamedLinkContainer {

  private String subPath;
  private String parentSpaceRootXdbLibrary;

  public String getSubPath() {
    return subPath;
  }

  public void setSubPath(String subPath) {
    this.subPath = subPath;
  }

  public String getParentSpaceRootXdbLibrary() {
    return parentSpaceRootXdbLibrary;
  }

  public void setParentSpaceRootXdbLibrary(String parentSpaceRootFolder) {
    this.parentSpaceRootXdbLibrary = parentSpaceRootFolder;
  }

}
