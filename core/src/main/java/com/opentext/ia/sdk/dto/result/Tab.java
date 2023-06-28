/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.result;

import java.util.ArrayList;
import java.util.List;

import com.opentext.ia.sdk.support.JavaBean;


public class Tab extends JavaBean {

  private String name;
  private String title;
  private String description;
  private List<Column> columns = new ArrayList<>();
  private boolean exportEnabled;
  private List<String> exportConfigurations = new ArrayList<>();

  public Tab() {
    this(null);
  }

  public Tab(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public void setColumns(List<Column> columns) {
    this.columns = new ArrayList<>(columns.size());
    this.columns.addAll(columns);
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

  public boolean isExportEnabled() {
    return exportEnabled;
  }

  public void setExportEnabled(boolean exportEnabled) {
    this.exportEnabled = exportEnabled;
  }

  public List<String> getExportConfigurations() {
    return exportConfigurations;
  }

  public void setExportConfigurations(List<String> exportConfigurations) {
    this.exportConfigurations = new ArrayList<>(exportConfigurations.size());
    this.exportConfigurations.addAll(exportConfigurations);
  }

}
