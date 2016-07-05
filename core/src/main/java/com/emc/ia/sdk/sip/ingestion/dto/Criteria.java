/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;


public class Criteria {

  private String name;
  private String label;
  private String type;
  private String pKeyMinAttr;
  private String pKeyMaxAttr;
  private String pKeyValuesAttr;
  private boolean indexed;

  public Criteria() {
  }

  public Criteria(String name, String label, String type, String pKeyMinAttr, String pKeyMaxAttr, String pKeyValuesAttr, boolean indexed) {
    setName(name);
    setLabel(label);
    setType(type);
    setpKeyMinAttr(pKeyMinAttr);
    setpKeyMaxAttr(pKeyMaxAttr);
    setpKeyValuesAttr(pKeyValuesAttr);
    setIndexed(indexed);
  }

  public String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public final void setLabel(String label) {
    this.label = label;
  }

  public String getType() {
    return type;
  }

  public final void setType(String type) {
    this.type = type;
  }

  public String getpKeyMinAttr() {
    return pKeyMinAttr;
  }

  public final void setpKeyMinAttr(String pKeyMinAttr) {
    this.pKeyMinAttr = pKeyMinAttr;
  }

  public String getpKeyMaxAttr() {
    return pKeyMaxAttr;
  }

  public final void setpKeyMaxAttr(String pKeyMaxAttr) {
    this.pKeyMaxAttr = pKeyMaxAttr;
  }

  public String getpKeyValuesAttr() {
    return pKeyValuesAttr;
  }

  public final void setpKeyValuesAttr(String pKeyValuesAttr) {
    this.pKeyValuesAttr = pKeyValuesAttr;
  }

  public boolean isIndexed() {
    return indexed;
  }

  public final void setIndexed(boolean indexed) {
    this.indexed = indexed;
  }

}
