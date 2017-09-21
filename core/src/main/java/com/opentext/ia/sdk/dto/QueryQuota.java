/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;


public class QueryQuota extends NamedLinkContainer {

  private int aipQuota;
  private int aiuQuota;
  private int dipQuota;

  public int getAipQuota() {
    return aipQuota;
  }

  public void setAipQuota(int aipQuota) {
    this.aipQuota = aipQuota;
  }

  public int getAiuQuota() {
    return aiuQuota;
  }

  public void setAiuQuota(int aiuQuota) {
    this.aiuQuota = aiuQuota;
  }

  public int getDipQuota() {
    return dipQuota;
  }

  public void setDipQuota(int dipQuota) {
    this.dipQuota = dipQuota;
  }

}
