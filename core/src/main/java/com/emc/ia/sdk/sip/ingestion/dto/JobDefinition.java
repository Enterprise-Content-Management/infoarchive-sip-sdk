/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class JobDefinition extends NamedLinkContainer {

  private String handlerName;

  public JobDefinition() {
    setHandlerName("ConfirmationJob");
  }

  public String getHandlerName() {
    return handlerName;
  }

  public final void setHandlerName(String handlerName) {
    this.handlerName = handlerName;
  }
}
