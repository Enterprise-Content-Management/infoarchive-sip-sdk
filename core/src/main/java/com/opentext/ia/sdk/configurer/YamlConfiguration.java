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


/**
 * InfoArchive server configuration in YAML format.
 */
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
    map.put(SERVER_URI, getString(source, SERVER, "uri"));
    map.put(SERVER_AUTENTICATON_TOKEN, getString(source, SERVER, AUTHENTICATION, "token"));
    map.put(SERVER_AUTHENTICATION_USER, getString(source, SERVER, AUTHENTICATION, "user"));
    map.put(SERVER_AUTHENTICATION_PASSWORD, getString(source, SERVER, AUTHENTICATION, "password"));
    map.put(SERVER_AUTHENTICATION_GATEWAY, getString(source, SERVER, AUTHENTICATION, "gateway"));
    map.put(SERVER_CLIENT_ID, getString(source, SERVER, AUTHENTICATION, "client_id"));
    map.put(SERVER_CLIENT_SECRET, getString(source, SERVER, AUTHENTICATION, "client_secret"));

    map.put(TENANT_NAME, getString(source, "tenant"));

    map.put(FEDERATION_NAME, getString(source, XDB, "federation", NAME));
    map.put(FEDERATION_BOOTSTRAP, getString(source, XDB, "federation", "uri"));
    map.put(FEDERATION_SUPERUSER_PASSWORD, getString(source, XDB, "federation", "password"));
    map.put(DATABASE_NAME, getString(source, XDB, "database", NAME));
    map.put(DATABASE_ADMIN_PASSWORD, getString(source, XDB, "database", "password"));

    map.put(APPLICATION_NAME, getString(source, "application", NAME));
    map.put(APPLICATION_CATEGORY, getString(source, "application", "category"));
    map.put(APPLICATION_DESCRIPTION, getString(source, "application", DESCRIPTION));

    map.put(HOLDING_NAME, getString(source, "holding", NAME));

    map.put(FILE_SYSTEM_FOLDER, getString(source, "file-system-folder", NAME));

    map.put(STORE_NAME, getString(source, STORE, NAME));
    map.put(STORE_STORETYPE, getString(source, STORE, "store-type"));
    map.put(STORE_FOLDER, getString(source, STORE, "folder"));
    map.put(STORE_TYPE, getString(source, STORE, TYPE));

    map.put(AIC_NAME, getString(source, AIC, NAME));
    map.put(CRITERIA_NAME, getString(source, AIC, CRITERIA, NAME));
    map.put(CRITERIA_LABEL, getString(source, AIC, CRITERIA, "label"));
    map.put(CRITERIA_TYPE, getString(source, AIC, CRITERIA, TYPE));
    map.put(CRITERIA_PKEYMINATTR, getString(source, AIC, CRITERIA, "pkeyminattr"));
    map.put(CRITERIA_PKEYMAXATTR, getString(source, AIC, CRITERIA, "pkeymaxattr"));
    map.put(CRITERIA_PKEYVALUESATTR, getString(source, AIC, CRITERIA, "pkeyvaluesattr"));
    map.put(CRITERIA_INDEXED, getString(source, AIC, CRITERIA, "indexed"));

    map.put(QUOTA_NAME, getString(source, QUOTA, NAME));
    map.put(QUOTA_AIU, getString(source, QUOTA, "aiu"));
    map.put(QUOTA_AIP, getString(source, QUOTA, "aip"));
    map.put(QUOTA_DIP, getString(source, QUOTA, "dip"));

    map.put(RETENTION_POLICY_NAME, getString(source, "retention-policy", NAME));

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
