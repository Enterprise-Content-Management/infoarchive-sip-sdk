/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class ConvertSingularReferenceToSequenceForCollectionReferences extends PathVisitor {

  private static final Map<String, Collection<String>> COLLECTION_REFERENCES_BY_PATH_REGEX
      = collectionReferencesByPathRegex();

  private static Map<String, Collection<String>> collectionReferencesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/.*", Arrays.asList("namespace"));
    result.put("/confirmations/\\d+", Arrays.asList("holding"));
    result.put("/holdings/\\d+", Arrays.asList("ingestNode"));
    result.put("/holdingCryptoes/\\d+", Arrays.asList("pdiCrypto"));
    result.put("/queries/\\d+", Arrays.asList("aic"));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+", Arrays.asList("exportConfiguration"));
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
    map.replace(type, English.plural(type), Arrays.asList(map.get(type)));
  }

}
