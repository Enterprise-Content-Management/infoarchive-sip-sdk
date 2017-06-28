/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;

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
    visit.getMap().entries()
        .filter(this::isSingular)
        .forEach(entry -> convertToSequence(entry, visit.getMap()));
  }

  private boolean isSingular(Entry entry) {
    String type = entry.getKey();
    if (type.endsWith("s") || "version".equals(type)) {
      return false;
    }
    return entry.getValue().isMap();
  }

  private void convertToSequence(Entry entry, YamlMap yaml) {
    String type = entry.getKey();
    yaml.remove(type)
        .put(English.plural(type), Arrays.asList(entry.getValue().toMap()));
  }

}
