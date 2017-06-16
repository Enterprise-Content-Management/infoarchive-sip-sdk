/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.Arrays;

import org.atteo.evo.inflector.English;

import com.opentext.ia.sdk.support.yaml.*;


class ConvertTopLevelSingularObjectsToSequences implements Visitor {

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    visit.getMap().entries()
        .filter(entry -> isSingular(entry))
        .forEach(entry -> convertToSequence(entry, visit.getMap()));
  }

  private boolean isSingular(Entry entry) {
    String type = entry.getKey();
    if (type.endsWith("s") || "version".equals(type)) {
      return false;
    }
    Value value = entry.getValue();
    if (!value.isMap()) {
      return false;
    }
    return value.toMap().containsKey("name");
  }

  private void convertToSequence(Entry entry, YamlMap yaml) {
    String type = entry.getKey();
    yaml.remove(type)
        .put(English.plural(type), Arrays.asList(entry.getValue().toMap()));
  }

}
