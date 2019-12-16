/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Fluent API for building XML documents in memory using a
 * <a href="https://www.w3.org/TR/WD-DOM/introduction.html">Document Object Model</a> (DOM).
 */
public class DomXmlBuilder implements XmlBuilder<Document> {

  private final Document document;
  private Node current;
  private String namespaceUri;

  /**
   * Start building an existing XML document at the given node.
   * @param node The node at which to start building the XML document
   */
  public DomXmlBuilder(Element node) {
    this.document = node.getOwnerDocument();
    this.current = node;
  }

  /**
   * Start building an existing XML document at the given node.
   * @param node The node at which to start building the XML document
   */
  public DomXmlBuilder(Document node) {
    this.document = node;
    this.current = document;
  }

  @Override
  public Document build() {
    return document;
  }

  @Override
  public XmlBuilder<Document> namespace(String uri) {
    namespaceUri = uri;
    return this;
  }

  @Override
  public XmlBuilder<Document> element(String name) {
    return element(document.createElementNS(namespaceUri, name));
  }

  private XmlBuilder<Document> element(Element element) {
    current = current.appendChild(element);
    return this;
  }

  @Override
  public XmlBuilder<Document> end() {
    current = current.getParentNode();
    return this;
  }

  @Override
  public XmlBuilder<Document> attribute(String name, String value, String namespace) {
    ((Element)current).setAttributeNS(namespace, name, value);
    return this;
  }

  @Override
  public XmlBuilder<Document> text(String text) {
    if (text != null) {
      current.appendChild(document.createTextNode(XmlUtil.escape(text)));
    }
    return this;
  }

  @Override
  public XmlBuilder<Document> xml(Element xml) {
    current.appendChild(document.importNode(xml, true));
    return this;
  }

  @Override
  public XmlBuilder<Document> cdata(String cdata) {
    current.appendChild(document.createCDATASection(cdata));
    return this;
  }

  @Override
  public String toString() {
    return XmlUtil.toString(build());
  }

}
