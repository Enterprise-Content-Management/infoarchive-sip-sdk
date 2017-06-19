/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.Visit;
import com.opentext.ia.sdk.yaml.core.Visitor;
import com.opentext.ia.sdk.yaml.core.YamlMap;


class ReplacePdiIndexNamespaceWithUri implements Visitor {

  private static final String PATH_SEPARATOR = "/";
  private static final String INDEXES = "indexes";
  private static final String PATH = "path";

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    visit.getMap().get("pdis").toList().stream()
        .map(Value::toMap)
        .map(map -> map.get("content"))
        .map(Value::toMap)
        .filter(map -> map.get("format").equals("yaml"))
        .map(map -> map.get("text", "data"))
        .flatMap(v -> v.toList().stream())
        .map(Value::toMap)
        .filter(map -> map.containsKey(INDEXES))
        .flatMap(map -> map.get(INDEXES).toList().stream())
        .map(Value::toMap)
        .filter(map -> map.containsKey(PATH))
        .forEach(map -> replaceNamespacePrefixInPath(visit.getRootMap(), map));
  }

  private void replaceNamespacePrefixInPath(YamlMap root, YamlMap index) {
    index.put(PATH, replacePrefixes(root, index.get(PATH)));
  }

  private String replacePrefixes(YamlMap root, Value path) {
    String result = Arrays.stream(path.toString().split(PATH_SEPARATOR))
        .map(part -> replacePrefix(root, part))
        .collect(Collectors.joining(PATH_SEPARATOR));
    int index = result.indexOf('[');
    if (index < 0) {
      return result;
    }
    return result.substring(0,  index + 1) + replacePrefix(root, result.substring(index + 1));
  }

  private String replacePrefix(YamlMap root, String pathPart) {
    String[] prefixAndText = pathPart.split(":");
    if (prefixAndText.length == 1) {
      return pathPart;
    }
    String text = Arrays.stream(prefixAndText)
        .skip(1)
        .collect(Collectors.joining(":"));
    return String.format("{%s}:%s",  NamespaceUri.byPrefix(root, prefixAndText[0]), text);
  }

}
