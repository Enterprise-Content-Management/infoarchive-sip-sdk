/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.xml;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.support.test.TestCase;


public abstract class XmlBuilderTestCase<T> extends TestCase {

  private static final char OPEN_ELEMENT = '<';
  private static final String CLOSE_ELEMENT_END = ">\n";

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
    result.append(OPEN_ELEMENT).append(name).append("/>\n");

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
    result.append(OPEN_ELEMENT).append(parent).append(">\n  <").append(child).append("/>\n</")
        .append(parent).append(CLOSE_ELEMENT_END);

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
    result.append(OPEN_ELEMENT).append(parent).append(" xmlns=\"").append(ns1).append("\">\n  <")
        .append(child1).append(" xmlns=\"").append(ns2).append("\"/>\n  <").append(child2).append("/>\n</")
        .append(parent).append(CLOSE_ELEMENT_END);

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
        .append(name2).append("=\"").append(value2).append("\">\n  <").append(child)
        .append(" ns1:").append(name3).append("=\"").append(value3).append("\"/>\n</").append(parent).append(">\n");

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
    result.append(OPEN_ELEMENT).append(root).append(' ').append(name).append("=\"&lt;&apos;&quot;&amp;&gt;\"/>\n");

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
    result.append(OPEN_ELEMENT).append(parent).append('>').append(text1).append("\n  <")
        .append(child).append('>').append(text2).append("</").append(child).append(">\n</")
        .append(parent).append(CLOSE_ELEMENT_END);

    getBuilder().element(parent)
        .text(text1)
        .element(child, text2)
    .end();

    assertXml();
  }

  @Test
  public void shouldIgnoreMissingText() {
    String root = aName();
    result.append(OPEN_ELEMENT).append(root).append("/>\n");

    getBuilder().element(root)
        .element(aName(), null)
    .end();

    assertXml();
  }

  @Test
  public void shouldEscapeReservedCharactersInText() {
    String root = aName();
    String text = "<'\"&>";
    result.append(OPEN_ELEMENT).append(root).append(">&lt;&apos;&quot;&amp;&gt;</").append(root).append(">\n");

    getBuilder().element(root, text);

    assertXml();
  }

  @Test
  public void shouldIgnoreEmptyCollection() {
    String root = aName();
    result.append(OPEN_ELEMENT).append(root).append("/>\n");

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
    result.append(OPEN_ELEMENT).append(root).append(">\n  <")
        .append(parent).append(">\n    <")
        .append(child1).append("/>\n  </")
        .append(parent).append(">\n  <")
        .append(parent).append(">\n    <")
        .append(child2).append("/>\n  </")
        .append(parent).append(">\n</")
        .append(root).append(CLOSE_ELEMENT_END);

    getBuilder().elements(root, parent, Arrays.asList(child1, child2), (item, b) -> b.element(item).end());

    assertXml();
  }

}
