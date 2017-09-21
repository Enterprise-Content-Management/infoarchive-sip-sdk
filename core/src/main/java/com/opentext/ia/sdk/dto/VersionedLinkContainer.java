/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class VersionedLinkContainer extends NamedLinkContainer {

  private Long version;

  public VersionedLinkContainer() {
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
