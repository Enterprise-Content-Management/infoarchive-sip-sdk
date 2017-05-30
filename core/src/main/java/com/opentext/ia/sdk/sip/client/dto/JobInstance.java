/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

public class JobInstance extends NamedLinkContainer {

  private String status;

  public JobInstance() {
    setStatus("SUCCESS");
  }

  public String getStatus() {
    return status;
  }

  public final void setStatus(String status) {
    this.status = status;
  }
}
