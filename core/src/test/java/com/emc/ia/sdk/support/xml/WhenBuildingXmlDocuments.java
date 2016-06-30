/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.xml;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.emc.ia.sdk.support.test.TestCase;


public class WhenBuildingXmlDocuments extends TestCase {

  @Test
  public void shouldCreateDocument() {
    String root = randomString();
    String namespace = randomString();
    String attributeName = randomString();
    String attributeValue = randomString();
    String child1 = randomString();
    String child2 = randomString();
    String text = randomString();

    Element element = XmlBuilder.newDocument()
        .namespace(namespace)
        .element(root)
            .attribute(attributeName, attributeValue)
            .element(child1)
            .end()
            .element(child2, text)
        .end()
        .build()
        .getDocumentElement();

    assertEquals("Namespace", namespace, element.getNamespaceURI());
    assertEquals("Root", root, element.getLocalName());
    assertEquals("Attribute", attributeValue, element.getAttributeNS(null, attributeName));

    Element child = XmlUtil.getFirstChildElement(element, child1);
    assertNotNull("Missing child1", child);

    child = XmlUtil.getFirstChildElement(element, child2);
    assertNotNull("Missing child2", child);
    assertEquals("Child2 text", text, child.getTextContent());
  }

  @Test
  public void shouldUpdateExistingDocument() {
    String child = randomString();
    Document document = XmlUtil.parse(new ByteArrayInputStream("<root/>".getBytes(StandardCharsets.UTF_8)));

    new XmlBuilder(document.getDocumentElement()).element(child);

    assertNotNull("Element not added", XmlUtil.getFirstChildElement(document.getDocumentElement(), child));
  }

  @Test
  public void shouldNotAddElementForEmptyCollection() {
    Element root = XmlBuilder.newDocument()
        .element(randomString())
            .elements(randomString(), randomString(), Collections.emptyList(), null)
        .end()
        .build()
        .getDocumentElement();

    assertNull("Added collection", root.getFirstChild());
  }

  @Test
  public void shouldAddElementsForCollectionItems() {
    String collectionName = randomString();
    String itemName = randomString();
    String item1 = collectionName;
    String item2 = collectionName;
    List<String> collection = Arrays.asList(item1, item2);

    Element root = XmlBuilder.newDocument()
        .element(collectionName)
            .elements(collectionName, itemName, collection, (item, builder) -> builder.element(item).end())
        .end()
        .build()
        .getDocumentElement();

    Element collectionElement = XmlUtil.getFirstChildElement(root, collectionName);
    assertNotNull("Missing collection element", collectionElement);

    Iterator<Element> itemElements = XmlUtil.namedElementsIn(collectionElement, itemName).iterator();
    assertTrue("Missing item #1", itemElements.hasNext());

    Element itemElement = itemElements.next();
    assertNotNull("Missing item #1 child", XmlUtil.getFirstChildElement(itemElement, item1));
    assertTrue("Missing item #2", itemElements.hasNext());

    itemElement = itemElements.next();
    assertNotNull("Missing item #2 child", XmlUtil.getFirstChildElement(itemElement, item2));
    assertFalse("Extra items", itemElements.hasNext());
  }

}
