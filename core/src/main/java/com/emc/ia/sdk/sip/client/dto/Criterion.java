/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.dto;

public class Criterion {

  private String name;
  private String label;
  private String type;
  private String pKeyMinAttr;
  private String pKeyMaxAttr;
  private String pKeyValuesAttr;
  private boolean indexed;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getpKeyMinAttr() {
    return pKeyMinAttr;
  }

  public void setpKeyMinAttr(String pKeyMinAttr) {
    this.pKeyMinAttr = pKeyMinAttr;
  }

  public String getpKeyMaxAttr() {
    return pKeyMaxAttr;
  }

  public void setpKeyMaxAttr(String pKeyMaxAttr) {
    this.pKeyMaxAttr = pKeyMaxAttr;
  }

  public String getpKeyValuesAttr() {
    return pKeyValuesAttr;
  }

  public void setpKeyValuesAttr(String pKeyValuesAttr) {
    this.pKeyValuesAttr = pKeyValuesAttr;
  }

  public boolean isIndexed() {
    return indexed;
  }

  public void setIndexed(boolean indexed) {
    this.indexed = indexed;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(128);
    builder.append("Criterion [name=");
    builder.append(name);
    builder.append(", label=");
    builder.append(label);
    builder.append(", type=");
    builder.append(type);
    builder.append(", pKeyMinAttr=");
    builder.append(pKeyMinAttr);
    builder.append(", pKeyMaxAttr=");
    builder.append(pKeyMaxAttr);
    builder.append(", pKeyValuesAttr=");
    builder.append(pKeyValuesAttr);
    builder.append(", indexed=");
    builder.append(indexed);
    builder.append(']');
    return builder.toString();
  }

}
