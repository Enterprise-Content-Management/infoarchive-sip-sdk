/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.ingest;

import static com.opentext.ia.sdk.server.configuration.InfoArchiveConfigurationProperties.*;

import java.util.Map;


public class ResultConfigBuilder {

  private final Map<String, String> values;
  private final SearchConfigBuilder parent;
  private final String searchName;
  private final String compositionName;

  public ResultConfigBuilder(SearchConfigBuilder parent, Map<String, String> values, String searchName,
      String compositionName) {
    this.parent = parent;
    this.values = values;
    this.searchName = searchName;
    this.compositionName = compositionName;
  }

  public ResultConfigBuilder mainColumn(String name, String label, String path, String type, String sort) {
    append(values, resolve(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME), name);
    append(values, resolve(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL), label);
    append(values, resolve(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH), path);
    append(values, resolve(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE), type);
    append(values, resolve(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT), sort);
    return this;
  }

  public ResultConfigBuilder mainColumn(String name, String label, String path, String type) {
    return mainColumn(name, label, path, type, "NONE");
  }

  public SearchConfigBuilder end() {
    return parent;
  }

  private String resolve(String keyTemplate) {
    return String.format(keyTemplate, searchName, compositionName);
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
