/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class JobDefinition extends NamedLinkContainer {

  private String handlerName;

// not a good practice
//  public JobDefinition() {
//    setHandlerName("ConfirmationJob");
//  }

  public String getHandlerName() {
    return handlerName;
  }

  public final void setHandlerName(String handlerName) {
    this.handlerName = handlerName;
  }

}
