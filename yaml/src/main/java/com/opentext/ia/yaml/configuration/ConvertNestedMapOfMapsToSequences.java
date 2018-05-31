/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;

class ConvertNestedMapOfMapsToSequences extends PathVisitor {

  private static final Map<String, Collection<String>> NESTED_SEQUENCES_BY_PATH_REGEX =
      nestedSequencesByPathRegex();

  ConvertNestedMapOfMapsToSequences() {
    super(NESTED_SEQUENCES_BY_PATH_REGEX.keySet());
  }

  private static Map<String, Collection<String>> nestedSequencesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/aics/\\d+", Collections.singletonList("criteria"));
    result.put("/ingests/\\d+/content/processors/\\d+/data", Collections.singletonList("indexes"));
    result.put("/pdis/\\d+/content/data/\\d+", Collections.singletonList("indexes"));
    result.put("/queries/\\d+/xdbPdiConfigs", Collections.singletonList("operands"));
    result.put("/resultConfigurationHelpers/\\d+/content", Collections.singletonList("data"));
    result.put("/resultConfigurationHelpers/\\d+/content/data/\\d+",
        Collections.singletonList("items"));
    result.put("/resultMasters/\\d+", Collections.singletonList("panels"));
    result.put("/resultMasters/\\d+/panels/\\d+", Collections.singletonList("tabs"));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+", Collections.singletonList("columns"));
    return result;
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    MapOfMapsToSequence replaceMapOfMapsWithSequence = new MapOfMapsToSequence(yaml);
    Collection<String> properties = pathRegexesMatching(visit)
        .flatMap(regex -> NESTED_SEQUENCES_BY_PATH_REGEX.get(regex).stream())
        .collect(Collectors.toList());
    yaml.entries().filter(
        entry -> properties.contains(entry.getKey()) && replaceMapOfMapsWithSequence.test(entry))
        .forEach(replaceMapOfMapsWithSequence);
  }

}
