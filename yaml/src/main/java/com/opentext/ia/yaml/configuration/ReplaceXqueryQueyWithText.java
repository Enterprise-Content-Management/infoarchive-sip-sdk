/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Collections;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


public class ReplaceXqueryQueyWithText extends PathVisitor {

  private static final String QUERY = "query";
  private static final String TEXT = "text";

  public ReplaceXqueryQueyWithText() {
    super(Collections.singleton("/(xquery|xqueries/[^/]+)"));
  }

  @Override
  public boolean test(Visit visit) {
    return super.test(visit) && !visit.getMap().get(QUERY, TEXT).isEmpty();
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    yaml.replace(QUERY, yaml.get(QUERY, TEXT));
  }

}
