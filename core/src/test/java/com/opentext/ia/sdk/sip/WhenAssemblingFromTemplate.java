/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.io.MemoryBuffer;
import com.opentext.ia.test.TestCase;


class WhenAssemblingFromTemplate extends TestCase {

  private static final String NL = System.getProperty("line.separator");

  @Test
  void shouldProvideHeaderAndFooter() throws IOException {
    String header = randomString();
    String footer = randomString();
    StringWriter output = new StringWriter();
    PrintWriter writer = new PrintWriter(output);
    Template<Map<String, ? extends Object>> template =
        new FixedHeaderAndFooterTemplate<Map<String, ? extends Object>>(header, footer) {
          @Override
          public void writeRow(Map<String, ? extends Object> values, Map<String, ContentInfo> contentInfo,
              PrintWriter printWriter) {
            // Do nothing
          }
        };

    template.writeHeader(writer);
    assertEquals(header + NL, output.toString(), "Header");

    template.writeFooter(writer);
    assertEquals(header + NL + footer + NL, output.toString(), "Footer");
  }

  @Test
  void shouldWriteTemplatedValues() throws IOException {
    Person p1 = newPerson(aName(), anEmail());
    Person p2 = newPerson(aName(), anEmail());
    @SuppressWarnings("unchecked")
    Template<Person> template = mock(Template.class);
    MemoryBuffer buffer = new MemoryBuffer();
    TemplatePdiAssembler<Person> assembler = new TemplatePdiAssembler<>(template, null);

    assembler.start(buffer);
    assembler.add(hashedContents(p1));
    assembler.add(hashedContents(p2));
    assembler.end();

    verify(template).writeHeader(any(PrintWriter.class));
    verify(template).writeRow(eq(p1), eq(Collections.emptyMap()), any(PrintWriter.class));
    verify(template).writeRow(eq(p2), eq(Collections.emptyMap()), any(PrintWriter.class));
    verify(template).writeFooter(any(PrintWriter.class));
  }

  private String aName() {
    return randomString(5) + ' ' + randomString(8);
  }

  private String anEmail() {
    return randomString(5) + '.' + randomString(8) + '@' + randomString(6) + ".com";
  }

  private Person newPerson(String name1, String email1) {
    return new Person(name1, email1);
  }

  private HashedContents<Person> hashedContents(Person p) {
    return new HashedContents<Person>(p, Collections.emptyMap());
  }

  public static class Person {

    private final String name;
    private final String emailAddress;

    Person(String name, String emailAddress) {
      this.name = name;
      this.emailAddress = emailAddress;
    }

    public String getName() {
      return name;
    }

    public String getEmailAddress() {
      return emailAddress;
    }

  }

}
