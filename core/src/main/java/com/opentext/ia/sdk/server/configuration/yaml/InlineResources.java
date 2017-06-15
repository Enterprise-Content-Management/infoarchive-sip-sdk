/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.opentext.ia.sdk.support.resource.ResourceResolver;
import com.opentext.ia.sdk.support.yaml.Value;
import com.opentext.ia.sdk.support.yaml.YamlMap;


class InlineResources implements Consumer<YamlMap> {

  private static final String RESOURCE = "resource";
  private static final String TEXT = "text";

  private final ResourceResolver resolver;

  InlineResources(ResourceResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void accept(YamlMap yaml) {
    if (yaml.containsKey(RESOURCE)) {
      yaml.put(TEXT, resolver.apply(yaml.get(RESOURCE).toString()));
      yaml.remove(RESOURCE);
    } else {
      acceptRecursively(yaml.values());
      acceptRecursively(yaml.values()
          .filter(Value::isList)
          .map(Value::toList)
          .flatMap(List::stream));
    }
  }

  private void acceptRecursively(Stream<Value> values) {
    values.filter(Value::isMap)
        .map(Value::toMap)
        .forEach(this::accept);
  }

}
