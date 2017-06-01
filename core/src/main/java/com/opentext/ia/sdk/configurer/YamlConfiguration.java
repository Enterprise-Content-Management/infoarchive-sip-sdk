/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.configurer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    filter(SERVER_AUTENTICATON_TOKEN, source, SERVER, AUTHENTICATION, "token");
    filter(SERVER_AUTHENTICATION_USER, source, SERVER, AUTHENTICATION, "user");
    filter(SERVER_AUTHENTICATION_PASSWORD, source, SERVER, AUTHENTICATION, "password");
    filter(SERVER_AUTHENTICATION_GATEWAY, source, SERVER, AUTHENTICATION, "gateway");
    filter(SERVER_CLIENT_ID, source, SERVER, AUTHENTICATION, "client_id");
    filter(SERVER_CLIENT_SECRET, source, SERVER, AUTHENTICATION, "client_secret");

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

    filter(FILE_SYSTEM_FOLDER, source, "file-system-folder", NAME);

    filter(STORE_NAME, source, STORE, NAME);
    filter(STORE_STORETYPE, source, STORE, "store-type");
    filter(STORE_FOLDER, source, STORE, "folder");
    filter(STORE_TYPE, source, STORE, TYPE);

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

    for (Map<String, Object> query: getList(source, "query")) {
      String name = getString(query, "name");
      append(QUERY_NAME, name);
      map.put(String.format(QUERY_NAMESPACE_PREFIX_TEMPLATE, name), getString(query, "namespace", "prefix"));
      map.put(String.format(QUERY_NAMESPACE_URI_TEMPLATE, name), getString(query, "namespace", "uri"));
      map.put(String.format(QUERY_RESULT_ROOT_ELEMENT_TEMPLATE, name), getString(query, "result", "root", "element"));
      map.put(String.format(QUERY_RESULT_ROOT_NS_ENABLED_TEMPLATE, name), getString(query, "result", "root", "ns-enabled"));
      String schema = getString(query, "result", "schema");
      map.put(String.format(QUERY_RESULT_SCHEMA_TEMPLATE, name), schema);
      map.put(String.format(QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, name), getString(query, "xdbpdi", "entity", "path"));
      map.put(String.format(QUERY_XDBPDI_SCHEMA_TEMPLATE, name), getString(query, "xdbpdi", "schema"));
      map.put(String.format(QUERY_XDBPDI_TEMPLATE_TEMPLATE, name), getString(query, "xdbpdi", "template"));
      map.put(String.format(QUERY_XDBPDI_OPERAND_NAME, name, schema), getString(query, "xdbpdi", "operand", NAME));
      map.put(String.format(QUERY_XDBPDI_OPERAND_PATH, name, schema), getString(query, "xdbpdi", "operand", "path"));
      map.put(String.format(QUERY_XDBPDI_OPERAND_TYPE, name, schema), getString(query, "xdbpdi", "operand", TYPE));
      map.put(String.format(QUERY_XDBPDI_OPERAND_INDEX, name, schema), getString(query, "xdbpdi", "operand", "index"));
    }

    for (Map<String, Object> resultHelper: getList(source, "result-helper")) {
      String name = getString(resultHelper, "name");
      append(RESULT_HELPER_NAME, name);
      map.put(String.format(RESULT_HELPER_SCHEMA_TEMPLATE, name), getString(resultHelper, "schema"));
      map.put(String.format(RESULT_HELPER_XML, name), getString(resultHelper, "xml"));
    }

    for (Map<String, Object> search: getList(source, "search")) {
      String searchName = getString(search, "name");
      append(SEARCH_NAME, searchName);
      map.put(String.format(SEARCH_DESCRIPTION, searchName), getString(search, DESCRIPTION));
      map.put(String.format(SEARCH_NESTED, searchName), getString(search, "nested"));
      map.put(String.format(SEARCH_STATE, searchName), getString(search, "state"));
      map.put(String.format(SEARCH_INUSE, searchName), getString(search, "inuse"));
      map.put(String.format(SEARCH_AIC, searchName), getString(search, "aic"));
      map.put(String.format(SEARCH_QUERY, searchName), getString(search, "query"));

      for (Map<String, Object> composition: getList(search, "composition")) {
        String compositionName = getString(composition, "name");
        append(SEARCH_COMPOSITION_NAME, compositionName);
        map.put(String.format(SEARCH_COMPOSITION_XFORM_NAME, searchName), getString(composition, "xform", NAME));
        map.put(String.format(SEARCH_COMPOSITION_XFORM, searchName), getString(composition, "xform", "xml"));
        map.put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME, searchName, compositionName), getString(composition, "result", "main", NAME));
        map.put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL, searchName, compositionName), getString(composition, "result", "main", "label"));
        map.put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH, searchName, compositionName), getString(composition, "result", "main", "path"));
        map.put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE, searchName, compositionName), getString(composition, "result", "main", "type"));
        map.put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT, searchName, compositionName), getString(composition, "result", "main", "sort"));
        map.put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_ENABLED_TEMPLATE, searchName), getString(composition, "export", "enabled"));
        map.put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_CONFIG_TEMPLATE, searchName), getString(composition, "export", "configs"));
      }
    }
  }

  private void filter(String key, Map<String, Object> source, String... vars) {
    String value = getString(source, vars);
    if (value != null && !value.isEmpty()) {
      map.put(key, value);
    }
  }

  private void append(String name, String newValue) {
    String value = map.get(name);
    if (value == null) {
      map.put(name, newValue);
    } else {
      map.put(name, value + "," + newValue);
    }
  }

  public Map<String, String> toMap() {
    return map;
  }

  private String getString(Map<String, Object> source, String... vars) {
    if (vars.length == 0) {
      throw new IllegalArgumentException("There is no second argument.");
    } else if (vars.length == 1) {
      Object obj = source.get(vars[0]);
      return (obj == null) ? "" : String.valueOf(obj);
    } else {
      Map<String, String> nearestMap = getMap(source, Arrays.copyOfRange(vars, 0, vars.length - 1));
      if (nearestMap == null || nearestMap.isEmpty()) {
        return "";
      }
      Object obj = nearestMap.get(vars[vars.length - 1]);
      return (obj == null) ? "" : String.valueOf(obj);
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

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getList(Map<String, Object> source, String var) {
    Object obj = source.get(var);
    return (obj == null) ? Collections.EMPTY_LIST : (List<Map<String, Object>>)obj;
  }

}
