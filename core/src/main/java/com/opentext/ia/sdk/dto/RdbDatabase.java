/*
 * Copyright (c) OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class RdbDatabase extends NamedLinkContainer {

  private String adminUser;
  private String adminPassword;
  private String rdbDataNodeName;
  private String rdbDataNodeBootstrap;
  private boolean inUse;

  public String getAdminPassword() {
    return adminPassword;
  }

  public void setAdminPassword(String adminPassword) {
    this.adminPassword = adminPassword;
  }

  public String getRdbDataNodeName() {
    return rdbDataNodeName;
  }

  public void setRdbDataNodeName(String rdbDataNodeName) {
    this.rdbDataNodeName = rdbDataNodeName;
  }

  public String getRdbDataNodeBootstrap() {
    return rdbDataNodeBootstrap;
  }

  public void setRdbDataNodeBootstrap(String rdbDataNodeBootstrap) {
    this.rdbDataNodeBootstrap = rdbDataNodeBootstrap;
  }

  public String getAdminUser() {
    return adminUser;
  }

  public void setAdminUser(String adminUser) {
    this.adminUser = adminUser;
  }

  public boolean isInUse() {
    return inUse;
  }

  public void setInUse(boolean inUse) {
    this.inUse = inUse;
  }

}
