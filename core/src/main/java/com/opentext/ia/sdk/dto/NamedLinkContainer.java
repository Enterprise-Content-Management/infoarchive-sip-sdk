/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import com.opentext.ia.sdk.support.http.rest.LinkContainer;

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
