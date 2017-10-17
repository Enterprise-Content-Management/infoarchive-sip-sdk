/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


public class IncludeExternalYaml implements Visitor {

  private static final String INCLUDES = "includes";
  private final ResourceResolver resourceResolver;

  public IncludeExternalYaml(ResourceResolver resourceResolver) {
    this.resourceResolver = resourceResolver;
  }

  @Override
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    yaml.get(INCLUDES).toList().forEach(value -> include(value.toString(), yaml));
    yaml.remove(INCLUDES);
  }

  private void include(String resource, YamlMap target) {
    YamlMap include = YamlMap.from(resourceResolver.apply(resource));
    include.entries().forEach(entry -> includeEntry(entry.getKey(), entry.getValue(), target));
  }

  private YamlMap includeEntry(String key, Value value, YamlMap target) {
    return target.put(key, value);
  }

}
