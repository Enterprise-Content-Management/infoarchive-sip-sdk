/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonFormatter {

  public String format(Object value) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return mapper.writer()
      .writeValueAsString(Objects.requireNonNull(value));
  }

}
