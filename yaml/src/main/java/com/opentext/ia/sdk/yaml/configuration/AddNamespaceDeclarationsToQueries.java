/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import java.util.stream.Collectors;

import com.opentext.ia.sdk.yaml.core.*;


class AddNamespaceDeclarationsToQueries implements Visitor {

  private static final String NL = System.getProperty("line.separator");
  private static final String NAMESPACES = "namespaces";

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    yaml.entries()
        .filter(new IsQuery())
        .filter(entry -> !entry.getValue().toMap().get(NAMESPACES).toList().isEmpty())
        .forEach(entry -> addNamespaceDeclarations(visit.getRootMap(), entry));
  }

  private void addNamespaceDeclarations(YamlMap root, Entry entry) {
    String namespaceDeclarations = entry.getValue().toMap()
        .get(NAMESPACES).toList().stream()
        .map(prefix -> namespaceDeclarationFor(root, prefix))
        .collect(Collectors.joining(NL)) + NL;
    entry.getParent()
        .put(entry.getKey(), namespaceDeclarations + entry.getValue().toMap().get("text"));
  }

  private String namespaceDeclarationFor(YamlMap root, Value prefix) {
    return String.format("declare namespace %s = \"%s\";", prefix, namespaceUriByPrefix(root, prefix));
  }

  private String namespaceUriByPrefix(YamlMap root, Value uri) {
    return root.get(NAMESPACES).toList().stream()
        .map(Value::toMap)
        .filter(m -> uri.equals(m.get("prefix")))
        .map(m -> m.get("uri").toString())
        .findAny()
        .orElse(uri.toString());
  }

}
