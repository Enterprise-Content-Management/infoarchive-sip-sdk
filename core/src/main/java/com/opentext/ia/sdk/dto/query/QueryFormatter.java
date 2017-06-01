/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class QueryFormatter {

  private final ObjectMapper mapper = new ObjectMapper();

  public QueryFormatter() {
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
  }

  public String format(SearchQuery request) {
    try {
      return mapper.writer()
        .writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to process JSON", e);
    }
  }

}
