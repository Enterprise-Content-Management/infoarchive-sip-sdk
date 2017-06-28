/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.stream.Stream;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;


class ReplaceIngestIndexNamespaceWithUri extends ReplaceIndexNamespaceWithUri {

  ReplaceIngestIndexNamespaceWithUri() {
    super("ingest");
  }

  @Override
  Stream<Value> getIndexParents(YamlMap content) {
    return content.get("processors").toList().stream()
        .map(Value::toMap)
        .map(map -> map.get("data"));
  }

}
