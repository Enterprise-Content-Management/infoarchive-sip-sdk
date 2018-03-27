/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class ExpandNamespaceUrisToNamespaceObjects extends PathVisitor {

  private static final String NAMESPACES = "namespaces";

  ExpandNamespaceUrisToNamespaceObjects() {
    super(Arrays.asList("/resultMasters/\\d++", "/queries/\\d+"));
  }

  @Override
  public boolean test(Visit visit) {
    if (!super.test(visit)) {
      return false;
    }
    List<Value> namespaces = visit.getMap().get(NAMESPACES).toList();
    return !namespaces.isEmpty() && namespaces.stream().allMatch(Value::isScalar);
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    yaml.put(NAMESPACES, yaml.get(NAMESPACES).toList().stream()
        .map(Value::toString)
        .map(prefix -> expand(visit.getRootMap(), prefix))
        .collect(Collectors.toList()));
  }

  private YamlMap expand(YamlMap root, String prefix) {
    return new YamlMap()
        .put("prefix", prefix)
        .put("uri", NamespaceUri.byPrefix(root, prefix));
  }

}
