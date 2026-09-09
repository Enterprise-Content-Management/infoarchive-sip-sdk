/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.query;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;



public class QueryFormatter {

  private final JsonMapper mapper;

  public QueryFormatter() {
    mapper = JsonMapper.builder()
    .configure(SerializationFeature.WRAP_ROOT_VALUE, true)
    .configure(SerializationFeature.INDENT_OUTPUT, false)
    .build();
  }

  public String format(SearchQuery request) {
    try {
      return mapper.writer()
        .writeValueAsString(request);
    } catch (JacksonException e) {
      throw new IllegalStateException("Failed to process JSON", e);
    }
  }

}
