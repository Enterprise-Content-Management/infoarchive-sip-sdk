/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Format an object as <a href="https://tools.ietf.org/html/rfc7159">JavaScript Object Notation</a> (JSON).
 */
public class JsonFormatter {

  public String format(Object value) throws IOException {
    JsonMapper mapper;

    mapper = JsonMapper.builder()
            .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
            .configure(SerializationFeature.INDENT_OUTPUT, false)
            // added if you want byte-identical output to the old Jackson 2 behavior
            // otherwise it will use the new behavior of writing dates as ISO-8601 strings
            .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();

    return mapper.writer()
        .writeValueAsString(Objects.requireNonNull(value));
  }

}
