/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.Visit;
import com.opentext.ia.sdk.yaml.core.Visitor;
import com.opentext.ia.sdk.yaml.core.YamlMap;


class ReplacePdiSchemaNamespaceWithName implements Visitor {

  private static final String NAME = "name";
  private static final String NAMESPACES = "namespaces";

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    visit.getMap().get("pdiSchemas").toList().stream()
        .map(Value::toMap)
        .filter(map -> !map.containsKey(NAME) && map.get(NAMESPACES).toList().size() == 1)
        .forEach(map -> replaceNamespaceWithName(visit.getRootMap(), map));
  }

  private void replaceNamespaceWithName(YamlMap root, YamlMap pdiSchema) {
    pdiSchema.put(NAME, NamespaceUri.byPrefix(root, pdiSchema.get(NAMESPACES).toList().iterator().next()))
        .remove(NAMESPACES);
  }

}
