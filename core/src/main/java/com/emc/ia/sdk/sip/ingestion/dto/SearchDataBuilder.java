/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto;

import com.emc.ia.sdk.support.xml.XmlBuilder;

public final class SearchDataBuilder {

  private final XmlBuilder builder;

  private SearchDataBuilder() {
    builder = XmlBuilder.newDocument().element("data");
  }

  public static SearchDataBuilder builder() {
    return new SearchDataBuilder();
  }

  private SearchDataBuilder criterion(String field, String operator, String value) {
    builder.element("criterion").element("name", field).element("operator", operator).element("value", value).end();
    return this;
  }

  public SearchDataBuilder equal(String filed, String value) {
    return criterion(filed, "EQUAL", value);
  }

  public SearchDataBuilder notEqual(String filed, String value) {
    return criterion(filed, "NOT_EQUAL", value);
  }

  public SearchDataBuilder startsWith(String filed, String value) {
    return criterion(filed, "STARTS_WITH", value);
  }

  public SearchDataBuilder endsWith(String filed, String value) {
    return criterion(filed, "ENDS_WITH", value);
  }

  public SearchDataBuilder contains(String filed, String value) {
    return criterion(filed, "CONTAINS", value);
  }

  public String build() {
    return builder.end().toString();
  }
}
