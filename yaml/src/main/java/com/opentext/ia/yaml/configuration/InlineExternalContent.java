/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.Visitor;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


class InlineExternalContent implements Visitor {

  private static final String CONTENT = "content";
  private static final String RESOURCE = "resource";
  private static final String TEXT = "text";

  private final ResourceResolver resolver;

  InlineExternalContent(ResourceResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public boolean test(Visit visit) {
    Value value = visit.getMap().get(CONTENT);
    return value.isMap() && value.toMap().get(RESOURCE).isScalar();
  }

  @Override
  public void accept(Visit visit) {
    YamlMap content = visit.getMap().get(CONTENT).toMap();
    content.put(TEXT, resolver.apply(content.get(RESOURCE).toString()));
    content.remove(RESOURCE);
  }

}
