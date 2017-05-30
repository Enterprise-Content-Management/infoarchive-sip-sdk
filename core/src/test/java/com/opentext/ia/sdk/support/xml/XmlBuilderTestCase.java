/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.opentext.ia.sdk.support.test.TestCase;


public abstract class XmlBuilderTestCase<T> extends TestCase {

  private static final String NL = System.getProperty("line.separator");
  private static final char OPEN_ELEMENT = '<';
  private static final char CLOSE_ELEMENT = '>';
  private static final String CLOSE_ELEMENT_NL = CLOSE_ELEMENT + NL;
  private static final String OPEN_CHILD_ELEMENT = "  " + OPEN_ELEMENT;
  private static final String CLOSE_ELEMENT_BEGIN = "</";

  protected abstract XmlBuilder<T> newBuilder();

  protected abstract String getOutput();

  private final StringBuilder result = new StringBuilder(256); // NOPMD AvoidStringBufferField
  private XmlBuilder<T> builder;

  @Before
  public void init() {
    this.builder = newBuilder();
  }

  protected XmlBuilder<T> getBuilder() {
    return builder;
  }

  @Test
  public void shouldPrintElementName() {
    String name = aName();
    result.append(OPEN_ELEMENT).append(name).append("/>").append(NL);

    getBuilder().element(name)
    .end();

    assertXml();
  }

  private String aName() {
    return randomString(5);
  }

  private void assertXml() {
    assertEquals("XML", result.toString(), getOutput());
  }

  @Test
  public void shouldPrintNestedElements() {
    String parent = aName();
    String child = aName();
    result.append(OPEN_ELEMENT).append(parent).append(CLOSE_ELEMENT_NL)
        .append(OPEN_CHILD_ELEMENT).append(child).append("/>").append(NL)
        .append(CLOSE_ELEMENT_BEGIN).append(parent).append(CLOSE_ELEMENT_NL);

    getBuilder().element(parent)
        .element(child)
        .end()
    .end();

    assertXml();
  }

  @Test
  public void shouldPrintNamespace() {
    String ns1 = aUri();
    String ns2 = aUri();
    String parent = aName();
    String child1 = aName();
    String child2 = aName();
    result.append(OPEN_ELEMENT).append(parent).append(" xmlns=\"").append(ns1).append("\">").append(NL).append(OPEN_CHILD_ELEMENT)
        .append(child1).append(" xmlns=\"").append(ns2).append("\"/>").append(NL).append(OPEN_CHILD_ELEMENT).append(child2)
        .append("/>").append(NL).append(CLOSE_ELEMENT_BEGIN).append(parent).append(CLOSE_ELEMENT_NL);

    getBuilder().namespace(URI.create(ns1))
        .element(parent)
            .namespace(ns2)
            .element(child1)
            .end()
            .namespace(ns1)
            .element(child2)
            .end()
        .end();

    assertXml();
  }

  private String aUri() {
    return "http://" + aName() + ".com/" + aName();
  }

  @Test
  public void shouldPrintAttribute() {
    String namespace = aUri();
    String parent = aName();
    String child = aName();
    String name1 = 'a' + aName();
    String name2 = 'z' + aName();
    String name3 = 'y' + aName();
    String value1 = aName();
    String value2 = aName();
    String value3 = aName();
    result.append('<').append(parent).append(' ')
        .append(name1).append("=\"").append(value1).append("\" xmlns:ns1=\"").append(namespace).append("\" ns1:")
        .append(name2).append("=\"").append(value2).append("\">").append(NL).append(OPEN_CHILD_ELEMENT).append(child)
        .append(" ns1:").append(name3).append("=\"").append(value3).append("\"/>").append(NL).append(CLOSE_ELEMENT_BEGIN)
        .append(parent).append(CLOSE_ELEMENT_NL);

    getBuilder().element(parent)
        .attribute(name1, value1)
        .attribute(name2, value2, namespace)
        .element(child)
            .attribute(name3, value3, namespace)
        .end()
    .end();

    assertXml();
  }

  @Test
  public void shouldEscapeReservedCharactersInAttribute() {
    String root = aName();
    String name = aName();
    String text = "<'\"&>";
    result.append(OPEN_ELEMENT).append(root)
        .append(' ').append(name).append("=\"&lt;&apos;&quot;&amp;&gt;\"/>").append(NL);

    getBuilder().element(root)
        .attribute(name, text)
    .end();

    assertXml();
  }

  @Test
  public void shouldPrintText() {
    String parent = aName();
    String text1 = aName();
    String child = aName();
    String text2 = aName();
    result.append(OPEN_ELEMENT).append(parent).append(CLOSE_ELEMENT).append(text1).append(NL).append(OPEN_CHILD_ELEMENT)
        .append(child).append(CLOSE_ELEMENT).append(text2).append(CLOSE_ELEMENT_BEGIN)
        .append(child).append(CLOSE_ELEMENT_NL).append(CLOSE_ELEMENT_BEGIN)
        .append(parent).append(CLOSE_ELEMENT_NL);

    getBuilder().element(parent)
        .text(text1)
        .element(child, text2)
    .end();

    assertXml();
  }

  @Test
  public void shouldIgnoreMissingText() {
    String root = aName();
    result.append(OPEN_ELEMENT).append(root).append("/>").append(NL);

    getBuilder().element(root)
        .element(aName(), null)
    .end();

    assertXml();
  }

  @Test
  public void shouldEscapeReservedCharactersInText() {
    String root = aName();
    String text = "<'\"&>";
    result.append(OPEN_ELEMENT).append(root)
        .append(">&lt;&apos;&quot;&amp;&gt;</").append(root).append(CLOSE_ELEMENT_NL);

    getBuilder().element(root, text);

    assertXml();
  }

  @Test
  public void shouldIgnoreEmptyCollection() {
    String root = aName();
    result.append(OPEN_ELEMENT).append(root).append("/>").append(NL);

    getBuilder().element(root)
        .elements(randomString(), randomString(), Collections.emptyList(), null)
    .end();

    assertXml();
  }

  @Test
  public void shouldAddElementsForCollectionItems() {
    String root = aName();
    String parent = aName();
    String child1 = aName();
    String child2 = aName();
    result.append(OPEN_ELEMENT).append(root).append(CLOSE_ELEMENT_NL).append(OPEN_CHILD_ELEMENT)
        .append(parent).append(CLOSE_ELEMENT_NL).append("    <")
        .append(child1).append("/>").append(NL).append("  </")
        .append(parent).append(CLOSE_ELEMENT_NL).append(OPEN_CHILD_ELEMENT)
        .append(parent).append(CLOSE_ELEMENT_NL).append("    <")
        .append(child2).append("/>").append(NL).append("  </")
        .append(parent).append(CLOSE_ELEMENT_NL).append(CLOSE_ELEMENT_BEGIN)
        .append(root).append(CLOSE_ELEMENT_NL);

    getBuilder().elements(root, parent, Arrays.asList(child1, child2), (item, b) -> b.element(item).end());

    assertXml();
  }

}
