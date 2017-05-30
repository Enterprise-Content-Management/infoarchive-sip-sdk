/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

import java.util.ArrayList;
import java.util.List;

public class RetentionClass {

  private String name;
  private List<String> policies;

  public RetentionClass() {
    setName("default");
    setPolicies(new ArrayList<>());
  }

  public String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public List<String> getPolicies() {
    return policies;
  }

  public final void setPolicies(List<String> policies) {
    this.policies = policies;
  }

}
