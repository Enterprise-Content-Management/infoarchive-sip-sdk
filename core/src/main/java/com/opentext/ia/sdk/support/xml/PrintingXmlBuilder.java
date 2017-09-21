/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.xml;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


/**
 * Fluent API for building XML documents by writing them to a {@linkplain PrintWriter}.
 */
public class PrintingXmlBuilder implements XmlBuilder<Void> {

  private final Stack<Element> elements = new Stack<>();
  private final PrintWriter writer;
  private String namespaceUri;
  private int prefixIndex = 1;
  private final String indent;

  public PrintingXmlBuilder(PrintWriter writer) {
    this(writer, "");
  }

  public PrintingXmlBuilder(PrintWriter writer, String indent) {
    this.writer = writer;
    this.indent = indent;
  }

  @Override
  public XmlBuilder<Void> namespace(String uri) {
    this.namespaceUri = uri;
    return this;
  }

  @Override
  public XmlBuilder<Void> element(String name) {
    Element parent = currentElement();
    if (parent != null) {
      close(parent, ElementContent.ELEMENT);
    }
    writer.print(indentation());
    writer.print('<');
    writer.print(name);
    if (namespaceUri != null && (parent == null || !parent.namespace.equals(namespaceUri))) {
      writer.print(" xmlns=\"");
      writer.print(namespaceUri);
      writer.print('"');
    }
    elements.push(new Element(namespaceUri, name));
    namespaceUri = null;
    return this;
  }

  private Element currentElement() {
    return elements.isEmpty() ? null : elements.peek();
  }

  private void close(Element element, ElementContent followedBy) {
    boolean wasHanging = element.isHanging();
    if (wasHanging && followedBy != ElementContent.EMPTY) {
      writer.println();
    }
    if (element.close()) {
      writer.print(followedBy.text);
      if (followedBy.needsNewLine) {
        writer.println();
      }
    } else if (followedBy == ElementContent.EMPTY) {
      if (!wasHanging) {
        writer.print(indentation());
      }
      writer.print("</");
      writer.print(element.name);
      writer.println('>');
    }
  }

  private String indentation() {
    StringBuilder result = new StringBuilder(indent);
    for (int i = elements.size(); i > 0; i--) {
      result.append("  ");
    }
    return result.toString();
  }

  @Override
  public XmlBuilder<Void> end() {
    close(elements.pop(), ElementContent.EMPTY);
    return this;
  }

  @Override
  public XmlBuilder<Void> attribute(String name, String value, String namespace) {
    writer.print(' ');
    if (namespace != null) {
      String prefix = namespaceToPrefix(namespace);
      writer.print(prefix);
      writer.print(':');
    }
    writer.print(name);
    writer.print("=\"");
    writer.print(XmlUtil.escape(value));
    writer.print('"');
    return this;
  }

  private String namespaceToPrefix(String namespace) {
    Element current = elements.peek();
    String result = current.prefixFor(namespace);
    if (result == null) {
      result = "ns" + prefixIndex++;
      writer.print("xmlns:");
      writer.print(result);
      writer.print("=\"");
      writer.print(namespace);
      writer.print("\" ");
      current.setPrefix(namespace, result);
    }
    return result;
  }

  @Override
  public XmlBuilder<Void> text(String text) {
    Element element = elements.peek();
    close(element, ElementContent.TEXT);
    writer.print(XmlUtil.escape(text));
    element.setHanging();
    return this;
  }

  @Override
  public Void build() {
    while (!elements.isEmpty()) {
      end();
    }
    writer.flush();
    return null;
  }


  enum ElementContent {
    EMPTY("/>", true), TEXT(">", false), ELEMENT(">", true);

    private final String text;
    private final boolean needsNewLine;

    ElementContent(String text, boolean needsNewLine) {
      this.text = text;
      this.needsNewLine = needsNewLine;
    }

  }


  class Element {

    private final Map<String, String> prefixesByNamespace = new HashMap<>();
    private final String name;
    private final String namespace;
    private boolean open;
    private boolean hanging;

    Element(String namespace, String name) {
      this.namespace = namespace;
      this.name = name;
      this.open = true;
    }

    void setPrefix(String uri, String prefix) {
      prefixesByNamespace.put(uri, prefix);
    }

    String prefixFor(String uri) {
      if (prefixesByNamespace.containsKey(uri)) {
        return prefixesByNamespace.get(uri);
      }
      int index = elements.indexOf(this);
      if (index == 0) {
        return null;
      }
      return elements.get(index - 1).prefixFor(uri);
    }

    boolean close() {
      boolean result = open;
      open = false;
      return result;
    }

    void setHanging() {
      hanging = true;
    }

    boolean isHanging() {
      boolean result = hanging;
      hanging = false;
      return result;
    }

  }

}
