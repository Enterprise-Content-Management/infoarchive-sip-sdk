/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

public class JobInstance extends NamedLinkContainer {

  private String status;

// not a good practice
//  public JobInstance() {
//    setStatus("SUCCESS");
//  }

  public String getStatus() {
    return status;
  }

  public final void setStatus(String status) {
    this.status = status;
  }

}
