/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;


class ConvertTopLevelMapOfMapsToSequences implements Visitor {

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    MapOfMapsToSequence replaceMapOfMapsWithSequence = new MapOfMapsToSequence(yaml);
    yaml.entries()
        .filter(replaceMapOfMapsWithSequence)
        .forEach(replaceMapOfMapsWithSequence);
  }

}
