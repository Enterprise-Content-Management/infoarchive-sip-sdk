/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

public class JobInstanceFilter {

  private boolean now;

  private String tenant;

  private String application;

  public String getTenant() {
    return this.tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  public String getApplication() {
    return this.application;
  }

  public void setApplication(String application) {
      this.application = application;
  }

  public boolean isNow() {
    return this.now;
  }

  public void setNow(boolean now) {
    this.now = now;
  }
}
