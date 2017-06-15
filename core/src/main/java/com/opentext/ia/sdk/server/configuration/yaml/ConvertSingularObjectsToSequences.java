/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.Arrays;
import java.util.function.Consumer;

import org.atteo.evo.inflector.English;

import com.opentext.ia.sdk.support.yaml.Entry;
import com.opentext.ia.sdk.support.yaml.YamlMap;


class ConvertSingularObjectsToSequences implements Consumer<YamlMap> {

  @Override
  public void accept(YamlMap yaml) {
    yaml.entries()
        .filter(entry -> isSingular(entry.getKey()))
        .forEach(entry -> convertToSequence(entry, yaml));
  }

  private boolean isSingular(String name) {
    return !name.endsWith("s") && !"version".equals(name);
  }

  private void convertToSequence(Entry entry, YamlMap yaml) {
    String type = entry.getKey();
    yaml.remove(type)
        .put(English.plural(type), Arrays.asList(entry.toMap()));
  }

}
