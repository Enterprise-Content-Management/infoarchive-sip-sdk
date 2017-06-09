/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.opentext.ia.sdk.support.io.StringStream;
import com.opentext.ia.sdk.support.resource.ResourceResolver;


/**
 * InfoArchive server configuration in YAML format.
 */
public class YamlConfiguration {

  private static final String RESOURCE = "resource";

  private final Map<String, Object> yaml = new HashMap<>();
  private Map<String, String> properties;

  public YamlConfiguration(File yaml) throws IOException {
    this(new FileInputStream(yaml), ResourceResolver.fromFile(yaml));
  }

  @SuppressWarnings("unchecked")
  public YamlConfiguration(InputStream yaml, ResourceResolver resolver) {
    for (Object data : new Yaml().loadAll(yaml)) {
      this.yaml.putAll((Map<String, Object>)data);
    }
    inlineResources(this.yaml, resolver);
  }

  public YamlConfiguration(String yaml) throws IOException {
    this(new StringStream(yaml), ResourceResolver.none());
  }

  @SuppressWarnings("unchecked")
  private void inlineResources(Map<String, Object> map, ResourceResolver resolver) {
    if (map.containsKey(RESOURCE)) {
      map.put("text", resolver.apply(map.get(RESOURCE).toString()));
      map.remove(RESOURCE);
    } else {
      for (Object value : map.values()) {
        if (value instanceof Map) {
          inlineResources((Map<String, Object>)value, resolver);
        }
      }
    }
  }

  public Map<String, String> toMap() {
    if (properties == null) {
      properties = new YamlPropertiesMap(yaml);
    }
    return properties;
  }

  public String get(String name) {
    return toMap().get(name);
  }

  @Override
  public String toString() {
    return new Yaml().dump(yaml);
  }

}
