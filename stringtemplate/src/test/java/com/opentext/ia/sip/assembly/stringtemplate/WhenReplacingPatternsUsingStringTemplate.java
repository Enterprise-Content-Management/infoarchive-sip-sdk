/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sip.assembly.stringtemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.sip.Template;
import com.opentext.ia.sdk.support.datetime.Dates;
import com.opentext.ia.test.TestCase;


class WhenReplacingPatternsUsingStringTemplate extends TestCase {

  @Test
  void shouldReplaceVariables() throws IOException {
    Date date = new Date();
    String name1 = randomString();
    String value1 = randomString();
    String name2 = randomString();
    String value2 = randomString();
    String name3 = randomString();
    String prefix = randomString();
    String infix = randomString();
    String suffix = randomString();
    Template<Map<String, Object>> template = new StringTemplate<>(randomString(), randomString(),
        prefix + "$model." + name1 + '$' + infix + "$model." + name2 + '$' + "$model." + name3 + '$' + suffix);
    Map<String, Object> values = new HashMap<>();
    values.put(name1, value1);
    values.put(name2, value2);
    values.put(name3, date);
    Writer actual = new StringWriter();

    template.writeRow(values, Collections.emptyMap(), new PrintWriter(actual));

    assertEquals(prefix + value1 + infix + value2 + Dates.toIso(date) + suffix, actual.toString(),
        "Text");
  }

}
