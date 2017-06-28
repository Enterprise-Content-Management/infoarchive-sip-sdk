/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class ReplacePdiResultSchemaNamespaceWithUri extends YamlContentVisitor {

  private static final String RESULT_SCHEMA = "result.schema";

  ReplacePdiResultSchemaNamespaceWithUri() {
    super("pdi");
  }

  @Override
  void visitContent(Visit visit, YamlMap content) {
    content.get(DATA).toList().stream()
        .map(Value::toMap)
        .filter(map -> map.containsKey(RESULT_SCHEMA))
        .forEach(map -> replaceResultSchemaNamespaceWithUri(visit.getRootMap(), map));
  }

  private void replaceResultSchemaNamespaceWithUri(YamlMap root, YamlMap data) {
    data.put(RESULT_SCHEMA, NamespaceUri.byPrefix(root, data.get(RESULT_SCHEMA)));
  }

}
