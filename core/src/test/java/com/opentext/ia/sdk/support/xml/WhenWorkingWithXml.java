/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

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

import com.opentext.ia.test.TestCase;


public class WhenWorkingWithXml extends TestCase {

  private static final String NL = System.getProperty("line.separator");

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void shouldPrettyPrintDocument() {
    String expected = XmlUtil.XML_DECLARATION + "<root>" + NL
        + "  <child attr=\"value\"/>" + NL
        + "  <!-- Another child -->" + NL
        + "  <child>" + NL
        + "    <grandChild/>" + NL
        + "  </child>" + NL
        + "  <?PI target=\"value\"?>" + NL
        + "  <![CDATA[<>]]>" + NL
        + "</root>" + NL;
    assertPrettyPrinted(expected);
  }

  private void assertPrettyPrinted(String expected) {
    Document document = XmlUtil.parse(toStream(expected));

    String actual = XmlUtil.toString(document);

    assertEquals("Formatted XML", expected, actual);
  }

  private InputStream toStream(String text) {
    return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void shouldSuppressPrintingRootNamespace() {
    String elementName = randomString(5);
    Document document = XmlBuilder.newDocument().namespace(randomString()).element(elementName).end().build();

    String actual = XmlUtil.toString(document.getDocumentElement(), false);

    assertEquals("Formatted XML", '<' + elementName + "/>" + NL, actual);
  }

  @Test
  public void shouldNotThrowExceptionOnValidDocumentWhenValidating() throws IOException {
    String elementName = randomString(8);
    Document document = XmlBuilder.newDocument().element(elementName).build();
    Document schema = someSchema(elementName);

    XmlUtil.validate(toStream(document), toStream(schema), randomString());
  }

  private Document someSchema(String elementName) {
    return XmlBuilder.newDocument().namespace(XMLConstants.W3C_XML_SCHEMA_NS_URI).element("schema").element("element")
        .attribute("name", elementName).attribute("type", "string").build();
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
