/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.Collection;
import java.util.Map;

import com.opentext.ia.sdk.support.yaml.Visit;
import com.opentext.ia.sdk.support.yaml.YamlMap;


public abstract class InsertDefaultVisitor extends PropertyVisitor {

  public InsertDefaultVisitor(Map<String, Collection<String>> propertiesByPathRegex) {
    super(propertiesByPathRegex);
  }

  @Override
  protected void visitProperty(Visit visit, String property) {
    YamlMap yaml = visit.getMap();
    if (!yaml.containsKey(property)) {
      yaml.put(property, getDefaultValueFor(visit, property));
    }
  }

  protected abstract String getDefaultValueFor(Visit visit, String property);

}
