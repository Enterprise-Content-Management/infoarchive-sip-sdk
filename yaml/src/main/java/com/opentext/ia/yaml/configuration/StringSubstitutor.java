/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.function.Function;

import com.opentext.ia.yaml.core.Entry;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;


public class StringSubstitutor implements Visitor {

  private final Function<String, String> substitutor;

  public StringSubstitutor(Function<String, String> substitutor) {
    this.substitutor = substitutor;
  }

  @Override
  public int maxNesting() {
    return substitutor == null ? 0 : Visitor.super.maxNesting();
  }

  @Override
  public boolean test(Visit visit) {
    return substitutor != null;
  }

  @Override
  public void accept(Visit visit) {
    boolean isResourceContainer = InlineExternalContent.RESOURCE_CONTAINER_PATHS.stream()
        .anyMatch(visit.getPath()::matches);
    visit.getMap().entries()
        .filter(entry -> entry.getValue().isString())
        .filter(entry -> !isResourceContainer || !InlineExternalContent.TEXT.equals(entry.getKey()))
        .forEach(this::substitute);
  }

  private void substitute(Entry entry) {
    entry.getParent().replace(entry.getKey(), substitutor.apply(entry.getValue().toString()));
  }

}
