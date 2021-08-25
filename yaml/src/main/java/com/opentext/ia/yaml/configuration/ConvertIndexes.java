/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


abstract class ConvertIndexes extends YamlContentVisitor {

  private static final String INDEXES = "indexes";
  private static final String TYPE = "type";

  ConvertIndexes(String type) {
    super(type);
  }

  @Override
  protected void visitContent(Visit visit, YamlMap content) {
    getIndexParents(content)
        .filter(Value::isMap)
        .map(Value::toMap)
        .forEach(indexParent -> {
      List<Value> indexes = indexParent.get(INDEXES).toList();
      if (hasIndexMaps(indexes)) {
        indexParent.put(INDEXES, convertIndexes(indexes));
      }
    });
  }

  abstract Stream<Value> getIndexParents(YamlMap content);

  private boolean hasIndexMaps(Collection<Value> indexes) {
    return !indexes.isEmpty() && indexes.stream()
        .map(Value::toMap)
        .filter(map -> map.get(TYPE).toString().endsWith(".index"))
        .count() == indexes.size();
  }

  private List<YamlMap> convertIndexes(Collection<Value> indexes) {
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
