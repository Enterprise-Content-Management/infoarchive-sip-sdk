/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sip.assembly.stringtemplate;

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

import com.emc.ia.sdk.sip.assembly.Template;
import com.emc.ia.sdk.support.datetime.Dates;
import com.emc.ia.sdk.support.test.TestCase;


public class WhenReplacingPatternsUsingStringTemplate extends TestCase {

  @Test
  public void shouldReplaceVariables() throws IOException {
    Date date = new Date();
    String name1 = randomString();
    String value1 = randomString();
    String name2 = randomString();
    String value2 = randomString();
    String name3 = randomString();
    String prefix = randomString();
    String infix = randomString();
    String suffix = randomString();
    Template<Map<String, Object>> template = new StringTemplate<Map<String, Object>>(randomString(), randomString(),
        prefix + "$model." + name1 + '$' + infix + "$model." + name2 + '$' + "$model." + name3 + '$' + suffix);
    Map<String, Object> values = new HashMap<>();
    values.put(name1, value1);
    values.put(name2, value2);
    values.put(name3, date);
    Writer actual = new StringWriter();

    template.writeRow(values, Collections.emptyMap(), new PrintWriter(actual));

    assertEquals("Text", prefix + value1 + infix + value2  + Dates.toIso(date) + suffix, actual.toString());
  }

}
