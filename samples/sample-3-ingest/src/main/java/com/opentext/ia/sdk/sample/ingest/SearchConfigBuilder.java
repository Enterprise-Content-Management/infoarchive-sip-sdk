/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.ingest;

import static com.opentext.ia.sdk.server.configuration.InfoArchiveConfiguration.*;

import java.util.HashMap;
import java.util.Map;


public class SearchConfigBuilder {

  private String name;
  private String aic;
  private String query;
  private String formXml;
  private String description;
  private String compositionName = "Set 1";
  private final Map<String, String> resultConfigValues = new HashMap<>();

  public SearchConfigBuilder name(String aName) {
    this.name = aName;
    return this;
  }

  public SearchConfigBuilder compositionName(String aName) {
    this.compositionName = aName;
    return this;
  }

  public SearchConfigBuilder description(String aDescription) {
    this.description = aDescription;
    return this;
  }

  public SearchConfigBuilder aic(String anAic) {
    this.aic = anAic;
    return this;
  }

  public SearchConfigBuilder query(String aQuery) {
    this.query = aQuery;
    return this;
  }

  public SearchConfigBuilder formXml(String theFormXml) {
    this.formXml = theFormXml;
    return this;
  }

  public ResultConfigBuilder result() {
    return new ResultConfigBuilder(this, resultConfigValues, name, compositionName);

  }

  public void build(Map<String, String> values) {
    append(values, SEARCH_NAME, name);
    values.put(resolve(SEARCH_DESCRIPTION), description);
    values.put(resolve(SEARCH_NESTED), "false");
    values.put(resolve(SEARCH_STATE), "DRAFT");
    values.put(resolve(SEARCH_INUSE), "true");
    values.put(resolve(SEARCH_AIC), aic);
    values.put(resolve(SEARCH_QUERY), query);
    // Only one composition for now
    values.put(resolve(SEARCH_COMPOSITION_NAME), compositionName);
    values.put(resolve(SEARCH_COMPOSITION_XFORM, compositionName), formXml);
    values.put(resolve(SEARCH_COMPOSITION_XFORM_NAME, compositionName), "form");
    values.putAll(resultConfigValues);
  }

  private String resolve(String keyTemplate) {
    return String.format(keyTemplate, name);
  }

  private String resolve(String keyTemplate, String other) {
    return String.format(keyTemplate, name, other);
  }

  private static void append(Map<String, String> result, String name, String newValue) {
    String value = result.get(name);
    if (value == null) {
      result.put(name, newValue);
    } else {
      result.put(name, value + "," + newValue);
    }
  }

}
