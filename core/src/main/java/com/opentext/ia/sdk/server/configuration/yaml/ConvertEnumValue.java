/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opentext.ia.sdk.support.yaml.Value;
import com.opentext.ia.sdk.support.yaml.Visit;
import com.opentext.ia.sdk.support.yaml.Visitor;
import com.opentext.ia.sdk.support.yaml.YamlMap;


class ConvertEnumValue implements Visitor {

  private static final Collection<String> TYPE = Arrays.asList("type");
  @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
  private static final Map<String, Collection<String>> ENUM_PROPERTIES_BY_PATH_REGEX = new HashMap() {{
    put("/applications/\\d", Arrays.asList("type", "archiveType"));
    put("/audits/\\d", Arrays.asList("eventName", "eventType"));
    put("/xdbLibraryPolicies/\\d", Arrays.asList("closeMode"));
    put("/retentionPolicies/\\d/agingStrategy", TYPE);
    put("/retentionPolicies/\\d/agingStrategy/agingPeriod", Arrays.asList("units"));
    put("/retentionPolicies/\\d/dispositionStrategy", TYPE);
    put("/holds/\\d/holdType", TYPE);
    put("/aics/\\d/criteria/\\d/", TYPE);
    put("/queries/\\d/xdbPdiConfigs/\\d/operands/\\d", TYPE);
    put("/resultConfigurationHelpers/\\d/content/text/data/\\d/", TYPE);
    put("/resultConfigurationHelpers/\\d/content/text/data/\\d/items/\\d", TYPE);
    put("/confirmations/\\d", Arrays.asList("types"));
    put("/appExportPipelines/\\d", Arrays.asList("inputFormat"));
    put("/appExportTransformations/\\d/", TYPE);
  }};

  @Override
  public boolean test(Visit visit) {
    return pathRegexesMatching(visit)
        .findAny()
        .isPresent();
  }

  private Stream<String> pathRegexesMatching(Visit visit) {
    return ENUM_PROPERTIES_BY_PATH_REGEX.keySet().stream()
        .filter(regex -> visit.getPath().matches('^' + regex + '$'));
  }

  @Override
  public void accept(Visit visit) {
    pathRegexesMatching(visit)
        .flatMap(regex -> ENUM_PROPERTIES_BY_PATH_REGEX.get(regex).stream())
        .forEach(property -> toEnum(visit.getMap(), property));
  }

  private void toEnum(YamlMap map, String property) {
    Value value = map.get(property);
    Object newValue = null;
    if (value.isScalar()) {
      newValue = toEnum(value.toString());
    } else if (value.isList()) {
      newValue = value.toList().stream()
          .map(Value::toString)
          .map(this::toEnum)
          .collect(Collectors.toList());
    }
    map.put(property, newValue);
  }

  private String toEnum(String text) {
    return text.toUpperCase(Locale.ENGLISH).replace(' ', '_');
  }

}
