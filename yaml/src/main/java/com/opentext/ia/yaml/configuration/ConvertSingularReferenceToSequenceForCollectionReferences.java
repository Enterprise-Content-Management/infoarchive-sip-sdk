/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;

class ConvertSingularReferenceToSequenceForCollectionReferences extends PathVisitor {

  private static final Map<String, Collection<String>> COLLECTION_REFERENCES_BY_PATH_REGEX =
      collectionReferencesByPathRegex();

  private static Map<String, Collection<String>> collectionReferencesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/.*", Collections.singletonList("namespace"));
    result.put("/confirmations/\\d+", Collections.singletonList("holding"));
    result.put("/holdings/\\d+", Collections.singletonList("ingestNode"));
    result.put("/holdingCryptoes/\\d+", Collections.singletonList("pdiCrypto"));
    result.put("/queries/\\d+", Collections.singletonList("aic"));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+",
        Collections.singletonList("exportConfiguration"));
    return result;
  }

  ConvertSingularReferenceToSequenceForCollectionReferences() {
    super(COLLECTION_REFERENCES_BY_PATH_REGEX.keySet());
  }

  @Override
  public void accept(Visit visit) {
    YamlMap map = visit.getMap();
    pathRegexesMatching(visit)
        .flatMap(regex -> COLLECTION_REFERENCES_BY_PATH_REGEX.get(regex).stream())
        .filter(map::containsKey)
        .forEach(property -> replaceSingleReferenceWithSequence(map, property));
  }

  private void replaceSingleReferenceWithSequence(YamlMap map, String type) {
    map.replace(type, English.plural(type), Collections.singletonList(map.get(type)));
  }

}
