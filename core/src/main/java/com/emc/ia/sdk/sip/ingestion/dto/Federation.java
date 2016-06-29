/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Federation extends NamedLinkContainer {

  private String superUserPassword;
  private String bootstrap;

  public String getSuperUserPassword() {
    return superUserPassword;
  }

  public void setSuperUserPassword(String superUserPassword) {
    this.superUserPassword = superUserPassword;
  }

  public String getBootstrap() {
    return bootstrap;
  }

  public void setBootstrap(String bootstrap) {
    this.bootstrap = bootstrap;
  }

}
