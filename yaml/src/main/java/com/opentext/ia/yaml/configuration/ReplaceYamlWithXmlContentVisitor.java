/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


abstract class ReplaceYamlWithXmlContentVisitor extends YamlContentVisitor {

  private static final String INDENT = "  ";
  private static final String NAMESPACES = "namespaces";

  enum StartTagOptions {
    CLOSE_TAG, NEW_LINE
  }

  private static final String NL = System.getProperty("line.separator");

  private final String rootTag;
  private final String itemTag;
  private final Object[] itemProperties;

  ReplaceYamlWithXmlContentVisitor(String type, String rootTag, String itemTag, Object... itemProperties) {
    super(type);
    this.rootTag = rootTag;
    this.itemTag = itemTag;
    this.itemProperties = itemProperties;
  }

  @Override
  void visitContent(Visit visit, YamlMap content) {
    String xml = translateToXml(visit.getRootMap(), content.get(itemProperties).toList(),
        content.get(NAMESPACES).toList());
    content
        .put("format", "xml")
        .remove(NAMESPACES)
        .put(TEXT, xml);
  }

  private String translateToXml(YamlMap root, Iterable<Value> items, List<Value> namespaces) {
    StringBuilder result = new StringBuilder();
    startTagWithNamespaces(root, rootTag, namespaces, result);
    items.forEach(item -> {
      startTag(itemTag, INDENT, EnumSet.allOf(StartTagOptions.class), result);
      item.toMap().entries().forEach(entry ->
          appendEntry(entry, "    ", result));
      endTag(itemTag, INDENT, result);
    });
    endTag(rootTag, "", result);
    return result.toString();
  }

  private void startTagWithNamespaces(YamlMap root, String tag, List<Value> namespaces, StringBuilder xml) {
    if (namespaces.isEmpty()) {
      startTag(tag, "", EnumSet.allOf(StartTagOptions.class), xml);
    } else {
      startTag(tag, "", EnumSet.noneOf(StartTagOptions.class), xml);
      String namespaceDeclarations = namespaces.stream()
          .map(Value::toString)
          .map(prefix -> String.format("xmlns:%s=\"%s\"", prefix, NamespaceUri.byPrefix(root, prefix)))
          .collect(Collectors.joining(" "));
      xml.append(' ').append(namespaceDeclarations).append('>').append(NL);
    }
  }

  private void startTag(String tag, String indent, Set<StartTagOptions> options, StringBuilder xml) {
    xml.append(indent).append('<').append(tag);
    if (options.contains(StartTagOptions.CLOSE_TAG)) {
      xml.append('>');
    }
    if (options.contains(StartTagOptions.NEW_LINE)) {
      xml.append(NL);
    }
  }

  private void endTag(String tag, String indent, StringBuilder xml) {
    xml.append(indent).append("</").append(tag).append('>').append(NL);
  }

  private void appendEntry(Entry entry, String indent, StringBuilder xml) {
    String property = entry.getKey();
    Value value = entry.getValue();
    if (value.isEmpty()) {
      appendEmpty(property, indent, xml);
    } else if (value.isScalar()) {
      appendScalar(property, value.toString().trim(), indent, xml);
    } else if (value.isList()) {
      appendList(property, value.toList(), indent, xml);
    } else {
      appendMap(property, value.toMap(), indent, xml);
    }
  }

  private void appendEmpty(String property, String indent, StringBuilder xml) {
    startTag(property, indent, EnumSet.noneOf(StartTagOptions.class), xml);
    xml.append("/>").append(NL);
  }

  private void appendScalar(String property, String value, String indent, StringBuilder xml) {
    startTag(property, indent, EnumSet.of(StartTagOptions.CLOSE_TAG), xml);
    if (value.contains("\n")) {
      xml.append("<![CDATA[").append(NL);
      for (String s : value.split("\\n")) {
        xml.append(indent).append(INDENT).append(s).append(NL);
      }
      xml.append(indent).append("]]></").append(property).append('>').append(NL);
    } else {
      xml.append(toXml(value));
      endTag(property, "", xml);
    }
  }

  private String toXml(String text) {
    return text.trim().replaceAll("<", "&lt;");
  }

  private void appendList(String property, List<Value> items, String indent, StringBuilder xml) {
    startTag(property, indent, EnumSet.allOf(StartTagOptions.class), xml);
    items.stream()
        .map(Value::toMap)
        .flatMap(YamlMap::entries)
        .forEach(entry -> appendEntry(entry, indent + INDENT, xml));
    endTag(property, indent, xml);
  }

  private void appendMap(String property, YamlMap map, String indent, StringBuilder xml) {
    startTag(property, indent, EnumSet.allOf(StartTagOptions.class), xml);
    map.entries()
        .forEach(entry -> appendEntry(entry, indent + INDENT, xml));
    endTag(property, indent, xml);
  }

}
