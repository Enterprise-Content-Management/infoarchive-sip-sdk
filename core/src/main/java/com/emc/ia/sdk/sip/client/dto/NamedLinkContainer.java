/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

import com.emc.ia.sdk.support.rest.LinkContainer;

public class NamedLinkContainer extends LinkContainer {

  private String name;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return String.format("name=%s; links=%s", name, super.toString());
  }

}
