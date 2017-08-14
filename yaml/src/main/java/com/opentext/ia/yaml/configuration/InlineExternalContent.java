/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.List;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


class InlineExternalContent extends PathVisitor {

  private static final List<String> RESOURCE_CONTAINER_PATHS = Arrays.asList(
      "/.+/content",
      "/customPresentationConfiguration(s)?/([^/]+/)?htmlTemplate");
  private static final String RESOURCE = "resource";
  private static final String TEXT = "text";

  private final ResourceResolver resolver;

  InlineExternalContent(ResourceResolver resolver) {
    super(RESOURCE_CONTAINER_PATHS);
    this.resolver = resolver;
  }

  @Override
  public boolean test(Visit visit) {
    return super.test(visit) && visit.getMap().containsKey(RESOURCE);
  }

  @Override
  public void accept(Visit visit) {
    YamlMap yaml = visit.getMap();
    yaml.put(TEXT, resolver.apply(yaml.get(RESOURCE).toString()));
    yaml.remove(RESOURCE);
  }

}
