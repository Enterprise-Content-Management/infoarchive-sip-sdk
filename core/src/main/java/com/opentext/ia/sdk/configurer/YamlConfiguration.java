/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.configurer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;


/**
 * InfoArchive server configuration in YAML format.
 */
public class YamlConfiguration {

  private final Map<String, String> map = new HashMap<>();

  public YamlConfiguration(File yaml) throws IOException {
    try (InputStream input = new FileInputStream(yaml)) {
      expand(input);
    }
  }

  public YamlConfiguration(InputStream yaml) {
    expand(yaml);
  }

  public YamlConfiguration(String yaml) throws IOException {
    try (InputStream input = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8))) {
      expand(input);
    }
  }

  @SuppressWarnings("unchecked")
  private void expand(InputStream input) {
    Map<String, Object> source = new HashMap<>();
    for (Object data : new Yaml().loadAll(input)) {
      source.putAll((Map<String, Object>)data);
    }
    expand(source);
  }

  @SuppressWarnings("PMD.UnusedFormalParameter")
  private void expand(Map<String, Object> source) {
    // TODO: Expand the YAML in [source] into [map]
  }

  public Map<String, String> toMap() {
    return map;
  }

}
