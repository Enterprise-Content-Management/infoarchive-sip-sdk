/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class AgingStrategy {

  private String type;
  private AgingPeriod agingPeriod;

  public AgingStrategy() {
    setType("DURATION");
    setAgingPeriod(new AgingPeriod());
  }

  public String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }

  public AgingPeriod getAgingPeriod() {
    return agingPeriod;
  }

  public final void setAgingPeriod(AgingPeriod agingPeriod) {
    this.agingPeriod = agingPeriod;
  }

}
