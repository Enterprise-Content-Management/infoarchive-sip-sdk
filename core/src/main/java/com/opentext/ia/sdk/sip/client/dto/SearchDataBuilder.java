/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.dto;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.opentext.ia.sdk.support.xml.XmlBuilder;


public final class SearchDataBuilder {

  private final StringWriter output = new StringWriter();
  private final XmlBuilder<?> builder;

  private SearchDataBuilder() {
    builder = XmlBuilder.newDocument(new PrintWriter(output)).element("data");
  }

  public static SearchDataBuilder builder() {
    return new SearchDataBuilder();
  }

  private SearchDataBuilder criterion(String field, String operator, String value) {
    builder.element("criterion")
        .element("name", field)
        .element("operator", operator)
        .element("value", value)
        .end();
    return this;
  }

  private SearchDataBuilder criterion(String field, String operator, String value1, String value2) {
    builder.element("criterion")
        .element("name", field)
        .element("operator", operator)
        .element("value", value1)
        .element("value", value2)
    .end();
    return this;
  }

  public SearchDataBuilder isEqual(String field, String value) {
    return criterion(field, "EQUAL", value);
  }

  public SearchDataBuilder isNotEqual(String field, String value) {
    return criterion(field, "NOT_EQUAL", value);
  }

  public SearchDataBuilder startsWith(String field, String value) {
    return criterion(field, "STARTS_WITH", value);
  }

  public SearchDataBuilder endsWith(String field, String value) {
    return criterion(field, "ENDS_WITH", value);
  }

  public SearchDataBuilder between(String field, String value1, String value2) {
    return criterion(field, "BETWEEN", value1, value2);
  }

  public SearchDataBuilder contains(String field, String value) {
    return criterion(field, "CONTAINS", value);
  }

  public String build() {
    builder.build();
    return output.toString();
  }

}
