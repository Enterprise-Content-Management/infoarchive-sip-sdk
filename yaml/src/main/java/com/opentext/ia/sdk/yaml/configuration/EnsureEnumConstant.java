/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import java.util.*;
import java.util.stream.Collectors;

import com.opentext.ia.sdk.yaml.core.PropertyVisitor;
import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.Visit;
import com.opentext.ia.sdk.yaml.core.YamlMap;


class EnsureEnumConstant extends PropertyVisitor {

  private static final Collection<String> TYPE = Arrays.asList("type");
  private static final Map<String, Collection<String>> ENUM_PROPERTIES_BY_PATH_REGEX = enumPropertiesByPathRegex();

  private static Map<String, Collection<String>> enumPropertiesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/applications/\\d", Arrays.asList("type", "archiveType"));
    result.put("/audits/\\d", Arrays.asList("eventName", "eventType"));
    result.put("/xdbLibraryPolicies/\\d", Arrays.asList("closeMode"));
    result.put("/retentionPolicies/\\d/agingStrategy", TYPE);
    result.put("/retentionPolicies/\\d/agingStrategy/agingPeriod", Arrays.asList("units"));
    result.put("/retentionPolicies/\\d/dispositionStrategy", TYPE);
    result.put("/holds/\\d/holdType", TYPE);
    result.put("/aics/\\d/criteria/\\d/", TYPE);
    result.put("/queries/\\d/xdbPdiConfigs/\\d/operands/\\d", TYPE);
    result.put("/resultConfigurationHelpers/\\d/content/text/data/\\d/", TYPE);
    result.put("/resultConfigurationHelpers/\\d/content/text/data/\\d/items/\\d", TYPE);
    result.put("/confirmations/\\d", Arrays.asList("types"));
    result.put("/appExportPipelines/\\d", Arrays.asList("inputFormat"));
    result.put("/appExportTransformations/\\d/", TYPE);
    return result;
  }

  EnsureEnumConstant() {
    super(ENUM_PROPERTIES_BY_PATH_REGEX);
  }

  @Override
  protected void visitProperty(Visit visit, String property) {
    YamlMap map = visit.getMap();
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
    Optional.ofNullable(newValue)
        .ifPresent(v -> map.put(property, v));
  }

  private String toEnum(String text) {
    return text.toUpperCase(Locale.ENGLISH).replace(' ', '_');
  }

}
