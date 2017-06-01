/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.result;

@SuppressWarnings("PMD.TooManyFields")
public class Column {

  public enum DefaultSort {
    NONE,
    ASCENDING,
    DESCENDING
  }

  public enum Type {
    XQUERY_REFERENCE,
    SCHEMA_COLUMN_NAME,
    CONTENT,
    NESTED_SEARCH,
    EXTERNAL_URL
  }

  public enum DataType {
    STRING,
    DATE,
    DATETIME,
    BOOLEAN,
    INTEGER,
    DOUBLE,
    LONG,
    FLOAT,
    ID,
    CID
  }

  private String name;

  private String label;

  private String xdbElementName;

  private boolean hidden;

  private boolean encrypt;

  private boolean masked;

  private boolean exportable = true;

  private DataType dataType;

  private Type type;

  private int order;

  private boolean sortable;

  private DefaultSort defaultSort;

  private String nestedSearchName;

  private String groupName;

  private String groupPath;

  private String path;

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

  public String getXdbElementName() {
    return xdbElementName;
  }

  public void setXdbElementName(String xdbElementName) {
    this.xdbElementName = xdbElementName;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public boolean isEncrypt() {
    return encrypt;
  }

  public void setEncrypt(boolean encrypt) {
    this.encrypt = encrypt;
  }

  public boolean isMasked() {
    return masked;
  }

  public void setMasked(boolean masked) {
    this.masked = masked;
  }

  public boolean isExportable() {
    return exportable;
  }

  public void setExportable(boolean exportable) {
    this.exportable = exportable;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public boolean isSortable() {
    return sortable;
  }

  public void setSortable(boolean sortable) {
    this.sortable = sortable;
  }

  public DefaultSort getDefaultSort() {
    return defaultSort;
  }

  public void setDefaultSort(DefaultSort defaultSort) {
    this.defaultSort = defaultSort;
  }

  public String getNestedSearchName() {
    return nestedSearchName;
  }

  public void setNestedSearchName(String nestedSearchName) {
    this.nestedSearchName = nestedSearchName;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getGroupPath() {
    return groupPath;
  }

  public void setGroupPath(String groupPath) {
    this.groupPath = groupPath;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public static Column fromSchema(String name, String label, String path, DataType dataType, DefaultSort sort) {
    Column column = new Column();
    column.setName(name);
    column.setLabel(label);
    column.setPath(path);
    column.setDataType(dataType);
    column.setDefaultSort(sort);
    column.setSortable(false);
    column.setType(Type.SCHEMA_COLUMN_NAME);
    return column;
  }

}
