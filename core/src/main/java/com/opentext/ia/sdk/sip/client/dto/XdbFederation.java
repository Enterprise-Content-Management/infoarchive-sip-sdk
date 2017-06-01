/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;


public class XdbFederation extends NamedLinkContainer {

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
