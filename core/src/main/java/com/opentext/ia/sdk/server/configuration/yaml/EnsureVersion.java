/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.function.Consumer;

import com.opentext.ia.sdk.support.yaml.YamlMap;


class EnsureVersion implements Consumer<YamlMap> {

  private static final String VERSION = "version";
  private static final String DEFAULT_VERSION = "1.0.0";

  @Override
  public void accept(YamlMap yaml) {
    if (!yaml.containsKey(VERSION)) {
      yaml.put(VERSION, DEFAULT_VERSION);
    }
  }

}
