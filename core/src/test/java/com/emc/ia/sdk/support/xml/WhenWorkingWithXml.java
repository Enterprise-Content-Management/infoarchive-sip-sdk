/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.xml;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.validation.ValidationException;
import javax.xml.XMLConstants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenWorkingWithXml extends TestCase {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldPrettyPrintDocument() {
    String expected = XmlUtil.XML_DECLARATION
        + "<root>\n  <child attr=\"value\"/>\n  <!-- Another child -->\n  <child>\n    <grandChild/>\n  </child>\n"
        + "  <?PI target=\"value\"?>\n  <![CDATA[<>]]>\n</root>\n";
    Document document = XmlUtil.parse(toStream(expected));

    String actual = XmlUtil.toString(document);

    assertEquals("Formatted XML", expected, actual);
  }

  private InputStream toStream(String text) {
    return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void shouldNotThrowExceptionOnValidDocumentWhenValidating() throws IOException {
    String elementName = randomString(8);
    Document document = XmlBuilder.newDocument().element(elementName).build();
    Document schema = someSchema(elementName);

    XmlUtil.validate(toStream(document), toStream(schema), randomString());
  }

  private Document someSchema(String elementName) {
    return XmlBuilder.newDocument()
        .namespace(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        .element("schema")
            .element("element")
                .attribute("name", elementName)
                .attribute("type", "string")
        .build();
  }

  private InputStream toStream(Document document) {
    return toStream(XmlUtil.toString(document));
  }

  @Test
  public void shouldThrowExceptionOnInvalidDocumentWhenValidating() throws IOException {
    Document document = XmlBuilder.newDocument().element(randomString(8)).build();
    Document schema = someSchema(randomString(8));

    thrown.expect(ValidationException.class);
    XmlUtil.validate(toStream(document), toStream(schema), randomString());
  }

  @Test
  public void shouldThrowExceptionOnInvalidSchemaWhenValidating() throws IOException {
    Document document = XmlBuilder.newDocument().element(randomString(8)).build();

    thrown.expect(ValidationException.class);
    XmlUtil.validate(toStream(document), toStream(document), randomString());
  }

}
