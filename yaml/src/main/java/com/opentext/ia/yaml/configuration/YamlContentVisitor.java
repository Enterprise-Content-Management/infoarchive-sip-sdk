/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;


abstract class YamlContentVisitor implements Visitor {

  protected static final String DATA = "data";
  protected static final String TEXT = "text";
  private final String type;

  YamlContentVisitor(String type) {
    this.type = type;
  }

  @Override
  @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract") // This *is* the implementation!
  public int maxNesting() {
    return 0;
  }

  @Override
  public void accept(Visit visit) {
    visit.getMap().get(English.plural(type)).toList().stream()
        .map(Value::toMap)
        .map(map -> map.get("content"))
        .map(Value::toMap)
        .filter(map -> map.get("format").toString().equals("yaml"))
        .findAny()
        .ifPresent(content -> visitContent(visit, content));
  }

  abstract void visitContent(Visit visit, YamlMap content);

}
