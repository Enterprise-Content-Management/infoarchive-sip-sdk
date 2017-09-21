/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import com.opentext.ia.sdk.support.JavaBean;


public class DispositionStrategy extends JavaBean {

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
