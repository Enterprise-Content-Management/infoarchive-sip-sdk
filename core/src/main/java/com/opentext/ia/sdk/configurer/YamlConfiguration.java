/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.configurer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;


public class YamlConfiguration implements InfoArchiveConfiguration {

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
    map.put(SERVER_URI, getString(source, "server", "uri"));
    map.put(SERVER_AUTENTICATON_TOKEN, getString(source, "server", "token"));

    map.put(FEDERATION_NAME, getString(source, "xdb", "federation", NAME));
    map.put(FEDERATION_BOOTSTRAP, getString(source, "xdb", "federation", "uri"));
    map.put(FEDERATION_SUPERUSER_PASSWORD, getString(source, "xdb", "federation", "password"));

    map.put(DATABASE_NAME, getString(source, "xdb", "database", NAME));
    map.put(DATABASE_ADMIN_PASSWORD, getString(source, "xdb", "database", "password"));

    map.put(RETENTION_POLICY_NAME, getString(source, "retention-policy", NAME));
    map.put(AIC_NAME, getString(source, "aic", NAME));
    map.put(QUOTA_NAME, getString(source, "quota", NAME));

    map.put(TENANT_NAME, getString(source, "tenant"));

    map.put(APPLICATION_NAME, getString(source, "application", NAME));
    map.put(APPLICATION_CATEGORY, getString(source, "application", "category"));
    map.put(APPLICATION_DESCRIPTION, getString(source, "application", "description"));

    map.put(HOLDING_NAME, getString(source, "holding", NAME));

    map.put(PDI_SCHEMA_NAME, getString(source, "pdi", "schema", NAME));
    map.put(PDI_SCHEMA, getString(source, "pdi", "schema", "xsd"));
    map.put(PDI_XML, getString(source, "pdi", "xml"));
    map.put(INGEST_XML, getString(source, "ingest", "xml"));
  }

  public Map<String, String> toMap() {
    return map;
  }

  private String getString(Map<String, Object> source, String... vars) {
    if (vars.length == 0) {
      throw new IllegalArgumentException("There is no second argument.");
    } else if (vars.length == 1) {
      return (String)source.get(vars[0]);
    } else {
      Map<String, String> nearestMap = getMap(source, Arrays.copyOfRange(vars, 0, vars.length - 1));
      if (nearestMap == null || nearestMap.isEmpty()) {
        return "";
      }
      return nearestMap.getOrDefault(vars[vars.length - 1], "");
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> getMap(Map<String, Object> source, String... vars) {
    if (vars.length == 0) {
      throw new IllegalArgumentException("There is no second argument.");
    } else if (vars.length == 1) {
      if (source == null) {
        return Collections.EMPTY_MAP;
      } else {
        return (Map<String, String>)source.get(vars[0]);
      }
    } else {
      return getMap((Map<String, Object>)source.get(vars[0]), Arrays.copyOfRange(vars, 1, vars.length));
    }
  }

}
