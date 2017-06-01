/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

import java.util.Objects;

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
  public DomXmlBuilder(Node node) {
    if (node instanceof Document) {
      this.document = (Document)node;
      this.current = document;
    } else if (node instanceof Element) {
      this.document = node.getOwnerDocument();
      this.current = node;
    } else {
      throw new IllegalArgumentException("Unhandled node type: " + Objects.requireNonNull(node).getNodeType());
    }
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
  public String toString() {
    return XmlUtil.toString(build());
  }

}
