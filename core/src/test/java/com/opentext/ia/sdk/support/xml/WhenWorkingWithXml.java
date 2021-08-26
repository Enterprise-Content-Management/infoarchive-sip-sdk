/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.validation.ValidationException;
import javax.xml.XMLConstants;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import com.opentext.ia.test.TestCase;


class WhenWorkingWithXml extends TestCase {

  private static final String NL = System.getProperty("line.separator");
  private static final String XML_WITH_CDATA =
      "<parent name=\"value\">" + NL
          + "  <child>" + NL
          + "    <grandChild>" + NL
          + "      <![CDATA[characters !&<>*\\n[[]] with markup]]>" + NL
          + "    </grandChild>" + NL
          + "  </child>" + NL
          + "</parent>" + NL;
  private static final String CDATA_VALUE = "characters !&<>*\\n[[]] with markup";

  @Test
  void shouldPrettyPrintDocument() {
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

    assertEquals(expected, actual, "Formatted XML");
  }

  private InputStream toStream(String text) {
    return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void shouldNotEscapeCdata() {
    Document document = XmlBuilder.newDocument()
        .namespace(randomString())
        .element("parent")
            .attribute("name", "value")
            .element("child")
                .element("grandChild")
                    .cdata(CDATA_VALUE)
                .end()
            .end()
        .end()
        .build();

    String actual = XmlUtil.toString(document.getDocumentElement(), false);

    assertEquals(XML_WITH_CDATA, actual, "Formatted XML");
  }

  @Test
  void shouldSuppressPrintingRootNamespace() {
    String elementName = randomString(5);
    Document document = XmlBuilder.newDocument().namespace(randomString()).element(elementName).end().build();

    String actual = XmlUtil.toString(document.getDocumentElement(), false);

    assertEquals('<' + elementName + "/>" + NL, actual, "Formatted XML");
  }

  @Test
  void shouldNotThrowExceptionOnValidDocumentWhenValidating() throws IOException {
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
  void shouldThrowExceptionOnInvalidDocumentWhenValidating() throws IOException {
    Document document = XmlBuilder.newDocument().element(randomString(8)).build();
    Document schema = someSchema(randomString(8));

    assertThrows(ValidationException.class,
        () -> XmlUtil.validate(toStream(document), toStream(schema), randomString()));
  }

  @Test
  void shouldThrowExceptionOnInvalidSchemaWhenValidating() throws IOException {
    Document document = XmlBuilder.newDocument().element(randomString(8)).build();

    assertThrows(ValidationException.class,
        () -> XmlUtil.validate(toStream(document), toStream(document), randomString()));
  }

  @Test
  void shouldEscapeOrRemoveInvalidCharacters() {
    assertEquals("a&apos;b&amp;c&quot;d&lt;e&gt;fgh\ni&#0009;j",
        XmlUtil.escape("a'b&c\"d<e>f\u0001g\u000Ch\ni\tj"), "Escaped text");
  }

}
