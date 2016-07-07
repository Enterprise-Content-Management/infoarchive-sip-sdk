/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class QueryFormatter {

  public String format(SearchQuery request) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
      mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
      return mapper.writer()
        .writeValueAsString(request);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
