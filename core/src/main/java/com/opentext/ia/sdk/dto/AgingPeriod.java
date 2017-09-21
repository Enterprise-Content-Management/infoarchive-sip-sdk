/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import com.opentext.ia.sdk.support.JavaBean;


public class AgingPeriod extends JavaBean {

  private String units;
  private int value;

  public AgingPeriod() {
    setUnits("DAYS");
    setValue(90);
  }

  public String getUnits() {
    return units;
  }

  public final void setUnits(String units) {
    this.units = units;
  }

  public int getValue() {
    return value;
  }

  public final void setValue(int value) {
    this.value = value;
  }

}
