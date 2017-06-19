/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.opentext.ia.sdk.yaml.core.Entry;
import com.opentext.ia.sdk.yaml.core.PathVisitor;
import com.opentext.ia.sdk.yaml.core.Visit;
import com.opentext.ia.sdk.yaml.core.YamlMap;


class ConvertNestedMapOfMapsToSequences extends PathVisitor {

  private static final Map<String, Collection<String>> NESTED_SEQUENCES_BY_PATH_REGEX = nestedSequencesByPathRegex();

  ConvertNestedMapOfMapsToSequences() {
    super(NESTED_SEQUENCES_BY_PATH_REGEX.keySet());
  }

  private static Map<String, Collection<String>> nestedSequencesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/pdis/\\d/content/text/data/\\d", Arrays.asList("indexes"));
    result.put("/queries/\\d/xdbPdiConfigs", Arrays.asList("operands"));
    result.put("/resultConfigurationHelpers/\\d/content/text", Arrays.asList("data"));
    result.put("/resultConfigurationHelpers/\\d/content/text/data/\\d", Arrays.asList("items"));
    return result;
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    Consumer<Entry> replaceMapOfMapsWithSequence = new MapOfMapsToSequence(yaml);
    Collection<String> properties = pathRegexesMatching(visit)
        .flatMap(regex -> NESTED_SEQUENCES_BY_PATH_REGEX.get(regex).stream())
        .collect(Collectors.toList());
    yaml.entries()
        .filter(entry -> properties.contains(entry.getKey()))
        .forEach(replaceMapOfMapsWithSequence);
  }

}
