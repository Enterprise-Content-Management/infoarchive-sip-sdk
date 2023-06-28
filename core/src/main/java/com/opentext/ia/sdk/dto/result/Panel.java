/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.result;

import java.util.ArrayList;
import java.util.List;

import com.opentext.ia.sdk.support.JavaBean;

public class Panel extends JavaBean {

  private String name;
  private String title;
  private String description;
  private List<Tab> tabs;

  public Panel() {
    this(null, new Tab[0]);
  }

  public Panel(String name) {
    this(name, new Tab[0]);
  }

  public Panel(String name, Tab... tabs) {
    this.name = name;
    this.tabs = new ArrayList<>(tabs.length);
    for (Tab tab : tabs) {
      this.tabs.add(tab);
    }
  }

  public Tab getTabByName(String tabName) {
    return tabs.stream()
        .filter(t -> tabName.equals(t.getName()))
        .findFirst()
        .orElse(null);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Tab> getTabs() {
    return tabs;
  }

  public void setTabs(List<Tab> tabs) {
    this.tabs = new ArrayList<>(tabs.size());
    this.tabs.addAll(tabs);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
