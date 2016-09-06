/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class DispositionStrategy {

  private String type;

  public DispositionStrategy() {
    setType("DESTROY_ALL");
  }

  public String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }

}
