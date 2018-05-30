/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Fluent API for building XML documents. Example usage:
 *
 * <pre>
 *
 * Document document = XmlBuilder.newDocument()
 *   .namespace("http://company.com/ns/example")
 *   .element("parent")
 *       .attribute("name", "value")
 *       .element("child")
 *           .element("grandChild")
 *           .end()
 *       .end()
 *       .element("child", "text")
 *   .end()
 *   .build();
 * </pre>
 * @param <T> The type of implementation for XML documents
 */
public interface XmlBuilder<T> {

  /**
   * Start building an XML document as a DOM.
   * @return A builder for an empty document
   */
  static XmlBuilder<Document> newDocument() {
    return new DomXmlBuilder(XmlUtil.newDocument());
  }

  /**
   * Start building an XML document as text.
   * @param writer The writer to print the XML document to
   * @return A builder for an empty document
   */
  static XmlBuilder<Void> newDocument(PrintWriter writer) {
    return newDocument(writer, "");
  }

  /**
   * Start building an XML document (fragment) as text.
   * @param writer The writer to print the XML document to
   * @param indent The indentation for the document (fragment)
   * @return A builder for an empty document
   */
  static XmlBuilder<Void> newDocument(PrintWriter writer, String indent) {
  return new PrintingXmlBuilder(writer, indent);
  }

  /**
   * Return the XML document that was built.
   * @return The XML document that was built
   */
  T build();

  /**
   * Set the <a href="http://www.w3.org/TR/REC-xml-names/">XML namespace</a> to the provided namespace URI for all
   * elements added afterwards.
   * @param uri The namespace URI
   * @return This builder
   */
  XmlBuilder<T> namespace(String uri);

  /**
   * Set the <a href="http://www.w3.org/TR/REC-xml-names/">XML namespace</a> to the provided namespace URI for all
   * elements added afterwards.
   * @param uri The namespace URI
   * @return This builder
   */
  default XmlBuilder<T> namespace(URI uri) {
    return namespace(uri.toString());
  }

  /**
   * Add an element at the current location in the XML document.
   * @param name The name (tag) of the element to add
   * @return This builder
   */
  XmlBuilder<T> element(String name);

  /**
   * End the current element in the XML document.
   * @return This builder
   */
  XmlBuilder<T> end();

  /**
   * Add an attribute to the current element in the XML document.
   * @param name The name of the attribute
   * @param value The value of the attribute
   * @return This builder
   */
  default XmlBuilder<T> attribute(String name, String value) {
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
  XmlBuilder<T> attribute(String name, String value, String namespace);

  /**
   * Add some text to the current element in the XML document.
   * @param text The text to add
   * @return This builder
   */
  XmlBuilder<T> text(String text);

  /**
   * Add an element containing the given text to the current location in the XML document. The element is automatically
   * closed. Note that this is a shorthand notation for
   *
   * <pre>
   * element(name).text(text)
   *   .end()
   * </pre>
   *
   * @param name The name/tag of the element to add
   * @param text The text to add to the element
   * @return This builder
   */
  default XmlBuilder<T> element(String name, String text) {
    return text == null ? this : element(name).text(text).end();
  }

  /**
   * Optionally add an element containing the given text to the current location in the XML document. The element is
   * automatically closed.
   *
   * @param name The name/tag of the element to add
   * @param maybeText The optional text to add to the element
   * @return This builder
   */
  default XmlBuilder<T> element(String name, Optional<String> maybeText) {
    maybeText.ifPresent(text -> element(name, text));
    return this;
  }

  /**
   * Add elements for a collection.
   * @param <I> The type of items in the collection
   * @param collectionName The name/tag for the collection
   * @param itemName The name/tag for an item in the collection
   * @param items The collection
   * @param itemBuilder A builder for an item in the collection
   * @return This builder
   */
  default <I> XmlBuilder<T> elements(String collectionName, String itemName, Iterable<I> items,
      BiConsumer<I, XmlBuilder<T>> itemBuilder) {
    return elements(collectionName, itemName, items.iterator(), itemBuilder);
  }

  /**
   * Add elements for a collection.
   * @param <I> The type of items in the collection
   * @param collectionName The name/tag for the collection
   * @param itemName The name/tag for an item in the collection
   * @param items The collection
   * @param itemBuilder A builder for an item in the collection
   * @return This builder
   */
  default <I> XmlBuilder<T> elements(String collectionName, String itemName, Iterator<I> items,
      BiConsumer<I, XmlBuilder<T>> itemBuilder) {
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

  /**
   * Add an XML document.
   * @param xml The XML document or fragment to add
   * @return This builder
   */
  default XmlBuilder<T> xml(InputStream xml) {
    return xml(new InputStreamReader(xml, StandardCharsets.UTF_8));
  }

  /**
   * Add an XML document.
   * @param xml The XML document or fragment to add
   * @return This builder
   */
  default XmlBuilder<T> xml(Reader xml) {
    return xml(XmlUtil.parse(xml).getDocumentElement());
  }

  /**
   * Add an XML element.
   * @param xml The XML element to add
   * @return This builder
   */
  XmlBuilder<T> xml(Element xml);

  /**
   * Add an XML document.
   * @param xml The XML document or fragment to add
   * @return This builder
   */
  default XmlBuilder<T> xml(String xml) {
    return xml(new StringReader(xml));
  }

}
