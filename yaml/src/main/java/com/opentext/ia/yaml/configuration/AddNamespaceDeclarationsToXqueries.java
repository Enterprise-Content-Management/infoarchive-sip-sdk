/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.stream.Collectors;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;


class AddNamespaceDeclarationsToXqueries implements Visitor {

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
        .replace(entry.getKey(), namespaceDeclarations + entry.getValue().toMap().get("text"))
        .remove(NAMESPACES);
  }

  private String namespaceDeclarationFor(YamlMap root, Value prefix) {
    return String.format("declare namespace %s = \"%s\";", prefix, NamespaceUri.byPrefix(root, prefix));
  }

}
