/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

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
