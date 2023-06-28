/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.util.ArrayList;
import java.util.List;

import com.opentext.ia.sdk.support.JavaBean;


public class RetentionClass extends JavaBean {

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
    this.policies = new ArrayList<>(policies.size());
    this.policies.addAll(policies);
  }

}
