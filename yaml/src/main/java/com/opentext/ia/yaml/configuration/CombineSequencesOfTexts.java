/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.core.YamlSequence;


/**
 * Inlining multiple resources using patterns may lead to undesirable structures, which this class corrects.
 * @see {@linkplain InlineExternalContent}
 */
class CombineSequencesOfTexts implements Visitor {

  private static final String TEXT = "text";

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    yaml.entries()
        .filter(this::isSequenceTextSequences)
        .map(Entry::getKey)
        .forEach(key -> combineSequencesOfTexts(yaml, key));
  }

  private boolean isSequenceTextSequences(Entry entry) {
    Value value = entry.getValue();
    if (!value.isList()) {
      return false;
    }
    return value.toList().stream().allMatch(this::isTextSequence);
  }

  private boolean isTextSequence(Value value) {
    return value.toMap().get(TEXT).isList();
  }

  private void combineSequencesOfTexts(YamlMap yaml, String key) {
    yaml.replace(key, key, combine(yaml.get(key).toList()));
  }

  private List<Object> combine(YamlSequence textSequence) {
    return textSequence.stream()
        .map(Value::toMap)
        .flatMap(this::toTextMaps)
        .collect(Collectors.toList());
  }

  private Stream<YamlMap> toTextMaps(YamlMap source) {
    return source.get(TEXT).toList().stream()
        .map(text -> YamlMap.from(source).put(TEXT, text));
  }

}
