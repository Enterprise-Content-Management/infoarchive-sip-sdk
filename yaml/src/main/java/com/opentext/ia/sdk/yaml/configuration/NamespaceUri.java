/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.YamlMap;


final class NamespaceUri {

  private NamespaceUri() {
    // Utility class
  }

  static String byPrefix(YamlMap yaml, Value prefix) {
    return byPrefix(yaml, prefix.toString());
  }

  static String byPrefix(YamlMap yaml, String prefix) {
    return yaml.get("namespaces").toList().stream()
        .map(Value::toMap)
        .filter(m -> m.get("prefix").equals(prefix))
        .map(m -> m.get("uri").toString())
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Missing namespace with prefix " + prefix));
  }

}
