/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto.query;

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
      return mapper.writer().writeValueAsString(request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
