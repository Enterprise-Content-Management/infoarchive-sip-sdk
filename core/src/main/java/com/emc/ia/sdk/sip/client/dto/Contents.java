/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class Contents extends ItemContainer<NamedLinkContainer> {

  public Contents() {
    super("contents");
  }

  public boolean hasContent() {
    return getItems().findAny()
      .isPresent();
  }

}
