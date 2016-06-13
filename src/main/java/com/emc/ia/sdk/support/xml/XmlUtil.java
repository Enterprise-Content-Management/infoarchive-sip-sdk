/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.support.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.validation.ValidationException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Generic XML processing functions.
 */
public final class XmlUtil { // NOPMD CyclomaticComplexity, StdCyclomaticComplexity

  /**
   * The <a href="http://www.w3.org/TR/2008/REC-xml-20081126/#sec-prolog-dtd">XML declaration</a> to be used in the
   * prolog of an XML document.
   */
  public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

  private XmlUtil() {
    // Utility class
  }

  private static final DocumentBuilderFactory VALIDATING_DOCUMENT_BUILDER_FACTORY = newSecureDocumentBuilderFactory();
  private static final ThreadLocal<DocumentBuilder> VALIDATING_DOCUMENT_BUILDER = new ThreadLocal<DocumentBuilder>() {
    @Override
    protected DocumentBuilder initialValue() {
      try {
        return VALIDATING_DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
        throw new IllegalStateException(e);
      }
    }
  };
  private static final String INDENT = "  ";

  /**
   * Return a new factory for building XML documents that is configured to operate securely. The factory also supports
   * <a href="http://www.w3.org/TR/REC-xml-names/">XML namespaces</a> and validation.
   * @return The newly created factory
   */
  public static DocumentBuilderFactory newSecureDocumentBuilderFactory() {
    try {
      DocumentBuilderFactory result = DocumentBuilderFactory.newInstance();
      result.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      result.setFeature("http://xml.org/sax/features/external-general-entities", false);
      result.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      result.setNamespaceAware(true);
      result.setValidating(true);
      return result;
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Return a new empty XML document.
   * @return The newly created document
   */
  public static Document newDocument() {
    DocumentBuilder documentBuilder = getDocumentBuilder();
    try {
      return documentBuilder.newDocument();
    } finally {
      documentBuilder.reset();
    }
  }

  private static DocumentBuilder getDocumentBuilder() {
    DocumentBuilder result = VALIDATING_DOCUMENT_BUILDER.get();
    result.setErrorHandler(new DefaultErrorHandler());
    return result;
  }

  /**
   * Convert a given node to a pretty-printed string.
   * @param node The node to convert
   * @return The pretty-printed string
   */
  public static String toString(Node node) {
    return toString(node, "");
  }

  public static String toString(Node node, String indentation) {
    StringBuilder result = new StringBuilder();
    if (node != null) {
      Map<String, String> namespaces = new HashMap<>();
      namespaces.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
      namespaces.put("http://www.w3.org/XML/1998/namespace", "xml");
      append(node, indentation, namespaces, result);
    }
    return result.toString();
  }

  private static void append(Node node, String indentation, Map<String, String> namespaces, // NOPMD CyclomaticComplexity
      StringBuilder builder) {
    switch (node.getNodeType()) {
      case Node.ELEMENT_NODE:
        appendElement(node, indentation, namespaces, builder);
        break;
      case Node.ATTRIBUTE_NODE:
        appendAttribute(node, namespaces, builder);
        break;
      case Node.TEXT_NODE:
        appendText(node, builder);
        break;
      case Node.CDATA_SECTION_NODE:
        appendCdataSection(node, indentation, builder);
        break;
      case Node.ENTITY_REFERENCE_NODE:
        appendEntityReferenceNode(node, builder);
        break;
      case Node.ENTITY_NODE:
        throw new IllegalStateException("Unhandled node of type Entity");
      case Node.PROCESSING_INSTRUCTION_NODE:
        appendProcessingInstruction(node, indentation, builder);
        break;
      case Node.COMMENT_NODE:
        appendComment(node, indentation, builder);
        break;
      case Node.DOCUMENT_NODE:
        appendDocument(node, indentation, namespaces, builder);
        break;
      case Node.DOCUMENT_TYPE_NODE:
        appendDocumentType(node, indentation, builder);
        break;
      case Node.DOCUMENT_FRAGMENT_NODE:
        throw new IllegalStateException("Unhandled node of type DocumentFragment");
      case Node.NOTATION_NODE:
        throw new IllegalStateException("Unhandled node of type Notation");
      default:
        throw new UnsupportedOperationException("Unhandled node type: " + node.getNodeType());
    }
  }

  private static void appendDocumentType(Node node, String indentation, StringBuilder builder) {
    DocumentType dtd = (DocumentType)node;
    builder.append("<!DOCTYPE ").append(dtd.getName());
    if (dtd.getPublicId() != null) {
      builder.append(" PUBLIC \"").append(dtd.getPublicId()).append("\" ");
    }
    if (dtd.getSystemId() != null) {
      builder.append(" \"").append(dtd.getSystemId()).append("\" ");
    }
    builder.append('[');
    if (dtd.getInternalSubset() != null) {
      builder.append('\n');
      for (String line : dtd.getInternalSubset().split("\\n")) {
        builder.append(indentation).append(INDENT).append(line).append('\n');
      }
      builder.append(indentation);
    }
    builder.append("]>\n");
  }

  private static void appendEntityReferenceNode(Node node, StringBuilder builder) {
    builder.append('&').append(node.getNodeName()).append(';');
  }

  private static void appendDocument(Node node, String indentation, Map<String, String> namespaces,
      StringBuilder builder) {
    builder.append(XML_DECLARATION);
    for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
      append(child, indentation, namespaces, builder);
    }
  }

  private static void appendElement(Node node, String indentation, Map<String, String> namespaces,
      StringBuilder builder) {
    Element element = (Element)node;
    openElement(indentation, builder, element);
    appendAttributes(indentation, element, namespaces, builder);
    if (node.getFirstChild() == null) {
      builder.append("/>\n");
    } else if (startsWithNonWhitespaceText(node)) {
      builder.append('>');
      appendChildren(node, indentation, namespaces, builder);
      closeElement(builder, element);
    } else {
      builder.append(">\n");
      appendChildren(node, indentation, namespaces, builder);
      builder.append(indentation);
      closeElement(builder, element);
    }
  }

  private static StringBuilder openElement(String indentation, StringBuilder builder, Element element) {
    String tag = element.getTagName();
    builder.append(indentation).append('<');
    builder.append(tag);
    if (hasDifferentNamespaceThanParent(element)) {
      int index = tag.indexOf(':');
      if (index < 0) {
        builder.append(" xmlns=\"").append(element.getNamespaceURI()).append('"');
      } else {
        builder.append(" xmlns:").append(tag.substring(0, index)).append("=\"").append(element.getNamespaceURI())
            .append('"');
      }
    }
    return builder;
  }

  private static boolean hasDifferentNamespaceThanParent(Node node) {
    if (node.getNamespaceURI() == null || isNamespaceNode(node)) {
      return false;
    }
    Node parent;
    if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
      parent = ((Attr)node).getOwnerElement();
    } else {
      parent = node.getParentNode();
    }
    if (parent == null) {
      return true;
    }
    return !node.getNamespaceURI().equals(parent.getNamespaceURI());
  }

  private static boolean isNamespaceNode(Node node) {
    return node.getNodeType() == Node.ATTRIBUTE_NODE && node.getNodeName().startsWith("xmlns")
        && "http://www.w3.org/2000/xmlns/".equals(node.getNamespaceURI());
  }

  private static void appendAttributes(String indentation, Element element, Map<String, String> namespaces,
      StringBuilder builder) {
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      append(attributes.item(i), indentation + INDENT, namespaces, builder);
    }
  }

  private static boolean startsWithNonWhitespaceText(Node node) {
    StringBuilder text = new StringBuilder();
    Node child = node.getFirstChild();
    while (child != null) {
      if (child.getNodeType() != Node.TEXT_NODE) {
        break;
      }
      text.append(child.getNodeValue());
      child = child.getNextSibling();
    }
    return text.length() > 0 && !text.toString().trim().isEmpty();
  }

  private static void appendChildren(Node node, String indentation, Map<String, String> namespaces,
      StringBuilder builder) {
    for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (child.getNodeType() != Node.ATTRIBUTE_NODE) {
        append(child, indentation + INDENT, namespaces, builder);
      }
    }
  }

  private static StringBuilder closeElement(StringBuilder builder, Element element) {
    return builder.append("</").append(element.getTagName()).append(">\n");
  }

  private static void appendAttribute(Node node, Map<String, String> namespaces, StringBuilder builder) {
    if (isNamespaceNode(node)) {
      return;
    }
    builder.append(' ');
    if (hasDifferentNamespaceThanParent(node)) {
      String uri = node.getNamespaceURI();
      String prefix = getPrefix(uri, namespaces);
      if (!"xml".equals(prefix)) {
        builder.append("xmlns:").append(prefix).append("=\"").append(uri).append("\" ");
      }
      builder.append(prefix).append(':');
    }
    builder.append(getAttributeName(node)).append("=\"").append(valueOf(node)).append('\"');
  }

  private static String getAttributeName(Node node) {
    String result = node.getLocalName();
    if (result == null) {
      // No namespace support while parsing
      result = node.getNodeName();
    }
    return result;
  }

  private static String getPrefix(String uri, Map<String, String> namespaces) {
    String result = namespaces.get(uri);
    if (result == null) {
      result = "ns" + (namespaces.size() + 1);
      namespaces.put(uri, result);
    }
    return result;
  }

  private static void appendText(Node node, StringBuilder builder) {
    builder.append(valueOf(node));
  }

  private static String valueOf(Node node) {
    return node.getNodeValue().trim();
  }

  private static void appendComment(Node node, String indentation, StringBuilder builder) {
    builder.append(indentation).append("<!-- ").append(valueOf(node)).append(" -->\n");
  }

  private static void appendProcessingInstruction(Node node, String indentation, StringBuilder builder) {
    builder.append(indentation).append("<?").append(node.getNodeName()).append(' ')
        .append(valueOf(node)).append("?>\n");
  }

  private static void appendCdataSection(Node node, String indentation, StringBuilder builder) {
    builder.append(indentation).append("<![CDATA[").append(valueOf(node)).append("]]>\n");
  }

  /**
   * Parse the content of a given file into an XML document.
   * @param file The file to parse
   * @return The parsed XML document
   */
  public static Document parse(File file) {
    if (!file.isFile()) {
      throw new IllegalArgumentException("Missing file: " + file.getAbsolutePath());
    }
    try {
      try (InputStream stream = new FileInputStream(file)) {
        return parse(stream);
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to parse " + file.getAbsolutePath(), e);
    }
  }

  /**
   * Parse the content of a given input stream into an XML document.
   * @param stream The input stream to parse
   * @return The parsed XML document
   */
  public static Document parse(InputStream stream) {
    DocumentBuilder documentBuilder = getDocumentBuilder();
    try {
      return documentBuilder.parse(stream);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      documentBuilder.reset();
    }
  }

  /**
   * Return the elements under a given parent element.
   * @param parent The parent of the elements to return
   * @return The children of the parent element
   */
  public static Stream<Element> elementsIn(Element parent) {
    return nodesIn(parent).filter(n -> n.getNodeType() == Node.ELEMENT_NODE).map(n -> (Element)n);
  }

  /**
   * Return the nodes under a given parent element.
   * @param parent The parent of the nodes to return
   * @return The children of the parent element
   */
  public static Stream<Node> nodesIn(Element parent) {
    return StreamSupport.stream(new ChildNodesSpliterator(parent), false);
  }

  /**
   * Return the first child element of a given parent element whose tag matches any of the given names. When no child
   * names are given, the first child element is returned.
   * @param parent The parent of the element to return
   * @param childNames The acceptable names of the children
   * @return The first child element that matches any of the names
   */
  public static Element getFirstChildElement(Element parent, String... childNames) {
    return firstOf(namedElementsIn(parent, childNames));
  }

  /**
   * Return the elements under a given parent element whose tag matches any of the given names. When no child
   * names are given, all child elements are returned.
   * @param parent The parent of the elements to return
   * @param childNames The acceptable names of the children
   * @return The children of the parent element
   */
  public static Stream<Element> namedElementsIn(Element parent, String... childNames) {
    return elementsIn(parent)
        .filter(e -> isName(e, childNames));
  }

  private static boolean isName(Element element, String... names) {
    if (names == null || names.length == 0) {
      return true;
    }
    if (names.length == 1 && names[0] == null) {
      return true;
    }
    String lookingFor = element.getLocalName();
    for (String name : names) {
      if (lookingFor.equals(name)) {
        return true;
      }
    }
    return false;
  }

  private static Element firstOf(Stream<Element> elements) {
    return elements.findFirst().orElse(null);
  }

  /**
   * Validate an XML document against an XML Schema document.
   * @param xml The XML document to validate
   * @param xmlSchema The XML Schema document to validate against
   * @param humanFriendlyDocumentType A human-friendly name that describes the schema
   * @throws IOException When an I/O error occurs
   */
  public static void validate(InputStream xml, InputStream xmlSchema, String humanFriendlyDocumentType)
      throws IOException {
    try {
      newXmlSchemaValidator(xmlSchema).validate(new StreamSource(Objects.requireNonNull(xml)));
    } catch (SAXException e) {
      throw new ValidationException("Invalid " + humanFriendlyDocumentType, e);
    }
  }

  public static Validator newXmlSchemaValidator(InputStream xmlSchema) {
    try {
      return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
          .newSchema(new StreamSource(xmlSchema))
          .newValidator();
    } catch (SAXException | NullPointerException e) { // NOPMD AvoidCatchingNPE - Want better error message
      throw new ValidationException("Invalid XML Schema", e);
    } finally {
      IOUtils.closeQuietly(xmlSchema);
    }
  }


  private static final class DefaultErrorHandler implements ErrorHandler {

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      // Do nothing
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      throw exception;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
      if (!isMissingSchemaError(exception)) {
        throw exception;
      }
    }

    private boolean isMissingSchemaError(SAXParseException exception) {
      String message = exception.getMessage();
      if (message == null) {
        return false;
      }
      return message.contains("no grammar found") || message.contains("must match DOCTYPE root \"null\"");
    }
  }

}
