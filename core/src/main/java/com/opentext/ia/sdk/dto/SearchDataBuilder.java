/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.opentext.ia.sdk.support.xml.XmlBuilder;


public final class SearchDataBuilder {

  private static final String CRITERION_ELEM = "criterion";
  private static final String NAME_ELEM = "name";
  private static final String OPERATOR_ELEM = "operator";
  private static final String VALUE_ELEM = "value";

  private final StringWriter output = new StringWriter();
  private final XmlBuilder<Void> xmlBuilder;

  public static SearchDataBuilder builder() {
    return new SearchDataBuilder();
  }

  private SearchDataBuilder() {
    xmlBuilder = XmlBuilder.newDocument(new PrintWriter(output)).element("data");
  }

  private SearchDataBuilder criterion(String field, String operator, String value) {
    xmlBuilder.element(CRITERION_ELEM)
        .element(NAME_ELEM, field)
        .element(OPERATOR_ELEM, operator)
        .element(VALUE_ELEM, value)
    .end();
    return this;
  }

  @SuppressWarnings("PMD.UseObjectForClearerAPI")
  private SearchDataBuilder criterion(String field, String operator, String value1, String value2) {
    xmlBuilder.element(CRITERION_ELEM)
        .element(NAME_ELEM, field)
        .element(OPERATOR_ELEM, operator)
        .element(VALUE_ELEM, value1)
        .element(VALUE_ELEM, value2)
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
    xmlBuilder.build();
    return output.toString();
  }

}
