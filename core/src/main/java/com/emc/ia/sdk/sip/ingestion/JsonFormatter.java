/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


public class JsonFormatter {

  public String format(Object value) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
      mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
      return mapper.writer().writeValueAsString(Objects.requireNonNull(value));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
