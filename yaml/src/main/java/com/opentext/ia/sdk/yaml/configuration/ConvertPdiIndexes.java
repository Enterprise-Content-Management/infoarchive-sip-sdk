/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import java.util.List;
import java.util.stream.Collectors;

import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.Visit;
import com.opentext.ia.sdk.yaml.core.YamlMap;


public class ConvertPdiIndexes extends YamlContentVisitor {

  private static final String INDEXES = "indexes";
  private static final String TYPE = "type";

  ConvertPdiIndexes() {
    super("pdi");
  }

  @Override
  void visitContent(Visit visit, YamlMap content) {
    content.get(DATA).toList().stream()
        .filter(Value::isMap)
        .map(Value::toMap)
        .forEach(data -> {
      List<Value> indexes = data.get(INDEXES).toList();
      if (hasIndexMaps(indexes)) {
        data.put(INDEXES, convertIndexes(indexes));
      }
    });
  }

  private boolean hasIndexMaps(List<Value> indexes) {
    return !indexes.isEmpty() && indexes.stream()
        .map(Value::toMap)
        .filter(map -> map.get(TYPE).toString().endsWith(".index"))
        .count() == indexes.size();
  }

  private List<YamlMap> convertIndexes(List<Value> indexes) {
    return indexes.stream()
        .map(Value::toMap)
        .map(this::convertIndex)
        .collect(Collectors.toList());
  }

  private YamlMap convertIndex(YamlMap index) {
    String type = index.get(TYPE).toString();
    index.remove(TYPE);
    return new YamlMap().put(type, index);
  }

}
