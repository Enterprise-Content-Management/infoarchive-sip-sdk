/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration.zip;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

import com.opentext.ia.yaml.core.YamlMap;


public class YamlZipEntryCustomization implements ZipEntryCustomization {

  private final BiConsumer<String, YamlMap> yamlCustomizer;
  private String name;

  public YamlZipEntryCustomization(BiConsumer<String, YamlMap> yamlCustomizer) {
    this.yamlCustomizer = yamlCustomizer;
  }

  @Override
  public boolean matches(String path) {
    this.name = path;
    return path.endsWith(".yml") || path.endsWith(".yaml");
  }

  @Override
  public InputStream customize(InputStream input) throws IOException {
    YamlMap yaml = YamlMap.from(input);
    yamlCustomizer.accept(name, yaml);
    return yaml.toStream();
  }

}
