/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class Library extends NamedLinkContainer {

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
