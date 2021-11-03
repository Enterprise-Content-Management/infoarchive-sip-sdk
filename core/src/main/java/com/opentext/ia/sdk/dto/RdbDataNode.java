/*
 * Copyright (c) OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class RdbDataNode extends NamedLinkContainer {

  private String userName;
  private String superUserPassword;
  private String bootstrap;
  private boolean inUse;

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

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public boolean isInUse() {
    return inUse;
  }

  public void setInUse(boolean inUse) {
    this.inUse = inUse;
  }

}
