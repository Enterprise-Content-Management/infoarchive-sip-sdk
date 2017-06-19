/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sip.assembly.velocity;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.opentext.ia.sdk.sip.Template;
import com.opentext.ia.sdk.support.datetime.Dates;
import com.opentext.ia.sdk.test.TestCase;


public class WhenReplacingPatternsUsingVelocity extends TestCase {

  @Test
  public void shouldReplaceVariables() throws IOException {
    String name1 = someString('n');
    String value1 = someString('1');
    String name2 = someString('n');
    String value2 = someString('2');
    String name3 = someString('n');
    Date value3 = new Date();
    String prefix = someString('p');
    String infix = someString(' ');
    String suffix = someString(' ');
    Template<Map<String, Object>> template = new VelocityTemplate<Map<String, Object>>(someString('a'), someString('a'),
        prefix + "$model." + name1 + infix + "$model." + name2 + " $isodate.format($model." + name3 + ')' + suffix);
    Map<String, Object> values = new HashMap<>();
    values.put(name1, value1);
    values.put(name2, value2);
    values.put(name3, value3);

    Writer actual = new StringWriter();

    template.writeRow(values, Collections.emptyMap(), new PrintWriter(actual));

    assertEquals("Text", prefix + value1 + infix + value2 + ' ' + Dates.toIso(value3) + suffix, actual.toString());
  }

  private String someString(char prefix) {
    return prefix + randomString(7);
  }

}
