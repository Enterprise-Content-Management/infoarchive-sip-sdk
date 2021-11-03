/*
 * Copyright (c) OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class Library extends NamedLinkContainer {

  private String subPath;
  private String type;

  public String getSubPath() {
    return subPath;
  }

  public void setSubPath(String subPath) {
    this.subPath = subPath;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}
