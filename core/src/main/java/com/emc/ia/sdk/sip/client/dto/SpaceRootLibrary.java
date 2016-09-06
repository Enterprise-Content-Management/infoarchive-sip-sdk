/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class SpaceRootLibrary extends NamedLinkContainer {

  private String xdbDatabase;

  public String getXdbDatabase() {
    return xdbDatabase;
  }

  public void setXdbDatabase(String xdbDatabase) {
    this.xdbDatabase = xdbDatabase;
  }

}
