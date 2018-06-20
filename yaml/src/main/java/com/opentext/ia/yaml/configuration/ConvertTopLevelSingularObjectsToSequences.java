/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;

class ConvertTopLevelSingularObjectsToSequences implements Visitor {

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    YamlMap map = visit.getMap();
    List<String> keys = map.entries()
        .filter(this::isSingular)
        .map(Entry::getKey)
        .collect(Collectors.toList());
    keys.forEach(key -> convertToSequence(key, map));
  }

  private boolean isSingular(Entry entry) {
    String type = entry.getKey();
    if (type.endsWith("s") || "version".equals(type)) {
      return false;
    }
    return entry.getValue().isMap();
  }

  private void convertToSequence(String type, YamlMap yaml) {
    YamlMap value = yaml.get(type).toMap();
    yaml.replace(type, English.plural(type), Collections.singletonList(value));
  }

}
