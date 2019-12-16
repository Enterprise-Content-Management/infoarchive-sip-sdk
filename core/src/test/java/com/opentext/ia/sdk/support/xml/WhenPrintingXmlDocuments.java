/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class WhenPrintingXmlDocuments extends XmlBuilderTestCase<Void> {

  private static final String NL = System.getProperty("line.separator");
  private static final String XML_WITH_CDATA =
      "<root name=\"value\">" + NL
          + "  <child>" + NL
          + "    <![CDATA[characters !&<>*\\n[[]] with markup]]>" + NL
          + "    <grandChild>" + NL
          + "      <![CDATA[characters !&<>*\\n[[]] with markup]]>" + NL
          + "    </grandChild>" + NL
          + "  </child>" + NL
          + "</root>" + NL;
  private static final String CDATA_VALUE = "characters !&<>*\\n[[]] with markup";

  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @Override
  protected XmlBuilder<Void> newBuilder() {
    return XmlBuilder.newDocument(new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8)));
  }

  @Override
  protected String getOutput() {
    getBuilder().build();
    try {
      return output.toString(StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  public void shouldPrintCdataElement() {
    newBuilder()
        .element("root")
        .attribute("name", "value")
            .element("child")
                .cdata(CDATA_VALUE)
                .element("grandChild")
                    .cdata(CDATA_VALUE)
            .end()
        .end()
        .build();

    assertEquals("Formatted XML", XML_WITH_CDATA, getOutput());
  }

}
