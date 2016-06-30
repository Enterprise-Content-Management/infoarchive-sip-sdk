/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.xml;

import java.net.URI;
import java.util.Iterator;
import java.util.function.BiConsumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Fluent API for building XML documents. Example usage:
 * <pre>Document document = XmlBuilder.newDocument()
    .namespace("http://company.com/ns/example")
    .element("parent")
        .attribute("name", "value")
        .element("child")
            .element("grandChild")
            .end()
        .end()
        .element("child", "text")
    .end()
    .build();
 * </pre>
 */
public class XmlBuilder {

  /**
   * Start building a new empty XML document.
   * @return A builder for an empty document
   */
  public static XmlBuilder newDocument() {
    return new XmlBuilder(XmlUtil.newDocument());
  }

  private final Document document;
  private Node current;
  private String namespaceUri;

  /**
   * Start building an existing XML document at the given node.
   * @param node The node at which to start building the XML document
   */
  public XmlBuilder(Node node) {
    if (node instanceof Document) {
      this.document = (Document)node;
      this.current = document;
    } else if (node instanceof Element) {
      this.document = node.getOwnerDocument();
      this.current = node;
    } else {
      throw new IllegalArgumentException("Unhandled node type: " + getNodeType(node));
    }
  }

  private Object getNodeType(Node node) {
    return node == null ? null : node.getNodeType();
  }

  /**
   * Return the XML document that was built.
   * @return The XML document that was built
   */
  public Document build() {
    return document;
  }

  /**
   * Set the <a href="http://www.w3.org/TR/REC-xml-names/">XML namespace</a> to the provided namespace URI for all
   * elements added afterwards.
   * @param uri The namespace URI
   * @return This builder
   */
  public XmlBuilder namespace(String uri) {
    namespaceUri = uri;
    return this;
  }

  /**
   * Set the <a href="http://www.w3.org/TR/REC-xml-names/">XML namespace</a> to the provided namespace URI for all
   * elements added afterwards.
   * @param uri The namespace URI
   * @return This builder
   */
  public XmlBuilder namespace(URI uri) {
    return namespace(uri.toString());
  }

  /**
   * Add an element at the current location in the XML document.
   * @param name The name (tag) of the element to add
   * @return This builder
   */
  public XmlBuilder element(String name) {
    return element(document.createElementNS(namespaceUri, name));
  }

  private XmlBuilder element(Element element) {
    current = current.appendChild(element);
    return this;
  }

  /**
   * End the current element in the XML document.
   * @return This builder
   */
  public XmlBuilder end() {
    current = current.getParentNode();
    return this;
  }

  /**
   * Add an attribute to the current element in the XML document.
   * @param name The name of the attribute
   * @param value The value of the attribute
   * @return This builder
   */
  public XmlBuilder attribute(String name, String value) {
    return attribute(name, value, null);
  }

  /**
   * Add an attribute to the current element in the XML document in a given
   * <a href="http://www.w3.org/TR/REC-xml-names/">XML namespace</a>.
   * @param name The name of the attribute
   * @param value The value of the attribute
   * @param namespace The URI of the XML namespace in which to add the attribute
   * @return This builder
   */
  public XmlBuilder attribute(String name, String value, String namespace) {
    ((Element)current).setAttributeNS(namespace, name, value);
    return this;
  }

  /**
   * Add some text to the current element in the XML document.
   * @param text The text to add
   * @return This builder
   */
  public XmlBuilder text(String text) {
    if (text != null) {
      current.appendChild(document.createTextNode(text));
    }
    return this;
  }

  /**
   * Add an element containing the given text to the current location in the XML document. The element is automatically
   * closed. Note that this is a shorthand notation for
   * <pre>element(name)
   *     .text(text)
   * .end()</pre>
   * @param name The name/tag of the element to add
   * @param text The text to add to the element
   * @return This builder
   */
  public XmlBuilder element(String name, String text) {
    return text == null ? this : element(name).text(text).end();
  }

  /**
   * Add elements for a collection.
   * @param <T> The type of items in the collection
   * @param collectionName The name/tag for the collection
   * @param itemName The name/tag for an item in the collection
   * @param items The collection
   * @param itemBuilder A builder for an item in the collection
   * @return This builder
   */
  public <T> XmlBuilder elements(String collectionName, String itemName, Iterable<T> items,
      BiConsumer<T, XmlBuilder> itemBuilder) {
    return elements(collectionName, itemName, items.iterator(), itemBuilder);
  }

  /**
   * Add elements for a collection.
   * @param <T> The type of items in the collection
   * @param collectionName The name/tag for the collection
   * @param itemName The name/tag for an item in the collection
   * @param items The collection
   * @param itemBuilder A builder for an item in the collection
   * @return This builder
   */
  public <T> XmlBuilder elements(String collectionName, String itemName, Iterator<T> items,
      BiConsumer<T, XmlBuilder> itemBuilder) {
    if (!items.hasNext()) {
      return this;
    }
    element(collectionName);
    while (items.hasNext()) {
      element(itemName);
      itemBuilder.accept(items.next(), this);
      end();
    }
    end();
    return this;
  }

  @Override
  public String toString() {
    return XmlUtil.toString(build());
  }

}
