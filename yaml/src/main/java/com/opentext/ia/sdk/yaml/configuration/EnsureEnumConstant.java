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

  private static final String TYPE = "type";
  private static final Collection<String> JUST_TYPE = Arrays.asList(TYPE);
  private static final Map<String, Collection<String>> ENUM_PROPERTIES_BY_PATH_REGEX = enumPropertiesByPathRegex();

  private static Map<String, Collection<String>> enumPropertiesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/aics/\\d+/criteria/\\d+", JUST_TYPE);
    result.put("/exportConfigurations/\\d+", JUST_TYPE);
    result.put("/exportPipelines/\\d+", Arrays.asList("inputFormat", TYPE));
    result.put("/exportTransformations/\\d+", JUST_TYPE);
    result.put("/applications/\\d+", Arrays.asList(TYPE, "archiveType"));
    result.put("/audits/\\d+", Arrays.asList("eventName", "eventType"));
    result.put("/confirmations/\\d+", Arrays.asList("types"));
    result.put("/holds/\\d+/holdType", JUST_TYPE);
    result.put("/queries/\\d+/xdbPdiConfigs/operands/\\d+", JUST_TYPE);
    result.put("/resultConfigurationHelpers/\\d+/content/data/\\d+", JUST_TYPE);
    result.put("/resultConfigurationHelpers/\\d+/content/data/\\d+/items/\\d+", JUST_TYPE);
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+/columns/\\d+", Arrays.asList("sort", TYPE));
    result.put("/retentionPolicies/\\d+/agingStrategy", JUST_TYPE);
    result.put("/retentionPolicies/\\d+/agingStrategy/agingPeriod", Arrays.asList("units"));
    result.put("/retentionPolicies/\\d+/dispositionStrategy", JUST_TYPE);
    result.put("/searches/\\d+", Arrays.asList("state"));
    result.put("/stores/\\d+", Arrays.asList("storeType"));
    result.put("/xdbLibraryPolicies/\\d+", Arrays.asList("closeMode"));
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
