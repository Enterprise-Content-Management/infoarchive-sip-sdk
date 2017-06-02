/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support;


public class VersionedJavaBean extends JavaBean {

  private Long version;

  public VersionedJavaBean() {
    super();
    setVersion(1L);
  }

  public Long getVersion() {
    return version;
  }

  public final void setVersion(Long version) {
    this.version = version;
  }

}
