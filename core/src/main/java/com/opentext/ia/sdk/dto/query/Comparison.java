/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opentext.ia.sdk.support.JavaBean;


@JsonPropertyOrder({ "name", "operator", "value" })
public class Comparison extends JavaBean implements Item {

  private final List<String> value;
  private String name;
  private Operator operator;

  public Comparison() {
    operator = Operator.EQUAL;
    value = new ArrayList<>();
  }

  public Comparison(String name, Operator operator, String value) {
    this(name, operator, Arrays.asList(Objects.requireNonNull(value, "Missing comparison value")));
  }

  public Comparison(String name, Operator operator, List<String> value) {
    this.name = name;
    this.operator = operator;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    Objects.requireNonNull(name);
    this.name = name;
  }

  public Operator getOperator() {
    return operator;
  }

  public void setOperator(Operator operator) {
    Objects.requireNonNull(operator);
    this.operator = operator;
  }

  public List<String> getValue() {
    return value;
  }

  public void setValue(List<String> value) {
    Objects.requireNonNull(value);
    this.value.addAll(value);
  }

}
