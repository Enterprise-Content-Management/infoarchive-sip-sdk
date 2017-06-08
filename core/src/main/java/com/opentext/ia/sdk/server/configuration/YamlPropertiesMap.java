/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration;

import java.util.*;


class YamlPropertiesMap extends HashMap<String, String> implements InfoArchiveConfiguration {

  private static final long serialVersionUID = -3961784010133931113L;
  private static final String SERVER = "server";
  private static final String AUTHENTICATION = "authentication";
  private static final String APPLICATION = "application";
  private static final String FEDERATION = "federation";
  private static final String STORE = "store";
  private static final String QUOTA = "quota";
  private static final String AIC = "aic";
  private static final String CRITERIA = "criteria";
  private static final String XDB = "xdb";
  private static final String SCHEMA = "schema";
  private static final String XML = "xml";
  private static final String RESULT = "result";
  private static final String XDBPDI = "xdbpdi";
  private static final String OPERAND = "operand";
  private static final String MAIN = "main";


  YamlPropertiesMap(Map<String, Object> yaml) {
    expand(yaml);
  }

  @SuppressWarnings("PMD.UnusedFormalParameter")
  private void expand(Map<String, Object> source) {
    put(SERVER_URI, getString(source, SERVER, "uri"));

    filter(SERVER_AUTENTICATON_TOKEN, source, SERVER, AUTHENTICATION, "token");
    filter(SERVER_AUTHENTICATION_USER, source, SERVER, AUTHENTICATION, "user");
    filter(SERVER_AUTHENTICATION_PASSWORD, source, SERVER, AUTHENTICATION, "password");
    filter(SERVER_AUTHENTICATION_GATEWAY, source, SERVER, AUTHENTICATION, "gateway");
    filter(SERVER_CLIENT_ID, source, SERVER, AUTHENTICATION, "client_id");
    filter(SERVER_CLIENT_SECRET, source, SERVER, AUTHENTICATION, "client_secret");

    put(TENANT_NAME, getString(source, "tenant"));

    put(FEDERATION_NAME, getString(source, XDB, FEDERATION, NAME));
    put(FEDERATION_BOOTSTRAP, getString(source, XDB, FEDERATION, "uri"));
    put(FEDERATION_SUPERUSER_PASSWORD, getString(source, XDB, FEDERATION, "password"));
    put(DATABASE_NAME, getString(source, XDB, "database", NAME));
    put(DATABASE_ADMIN_PASSWORD, getString(source, XDB, "database", "password"));

    put(APPLICATION_NAME, getString(source, APPLICATION, NAME));
    put(APPLICATION_CATEGORY, getString(source, APPLICATION, "category"));
    put(APPLICATION_DESCRIPTION, getString(source, APPLICATION, DESCRIPTION));

    put(HOLDING_NAME, getString(source, "holding", NAME));

    filter(FILE_SYSTEM_FOLDER, source, "file-system-folder", NAME);

    filter(STORE_NAME, source, STORE, NAME);
    filter(STORE_STORETYPE, source, STORE, "store-type");
    filter(STORE_FOLDER, source, STORE, "folder");
    filter(STORE_TYPE, source, STORE, TYPE);

    put(AIC_NAME, getString(source, AIC, NAME));
    put(CRITERIA_NAME, getString(source, AIC, CRITERIA, NAME));
    put(CRITERIA_LABEL, getString(source, AIC, CRITERIA, "label"));
    put(CRITERIA_TYPE, getString(source, AIC, CRITERIA, TYPE));
    put(CRITERIA_PKEYMINATTR, getString(source, AIC, CRITERIA, "pkeyminattr"));
    put(CRITERIA_PKEYMAXATTR, getString(source, AIC, CRITERIA, "pkeymaxattr"));
    put(CRITERIA_PKEYVALUESATTR, getString(source, AIC, CRITERIA, "pkeyvaluesattr"));
    put(CRITERIA_INDEXED, getString(source, AIC, CRITERIA, "indexed"));

    put(QUOTA_NAME, getString(source, QUOTA, NAME));
    put(QUOTA_AIU, getString(source, QUOTA, "aiu"));
    put(QUOTA_AIP, getString(source, QUOTA, "aip"));
    put(QUOTA_DIP, getString(source, QUOTA, "dip"));

    put(RETENTION_POLICY_NAME, getString(source, "retention-policy", NAME));

    put(PDI_SCHEMA_NAME, getString(source, "pdi", SCHEMA, NAME));
    put(PDI_SCHEMA, getString(source, "pdi", SCHEMA, "xsd"));
    put(PDI_XML, getString(source, "pdi", XML));
    put(INGEST_XML, getString(source, "ingest", XML));

    for (Map<String, Object> query: getList(source, "query")) {
      String name = getString(query, NAME);
      append(QUERY_NAME, name);
      put(String.format(QUERY_NAMESPACE_PREFIX_TEMPLATE, name), getString(query, "namespace", "prefix"));
      put(String.format(QUERY_NAMESPACE_URI_TEMPLATE, name), getString(query, "namespace", "uri"));
      put(String.format(QUERY_RESULT_ROOT_ELEMENT_TEMPLATE, name), getString(query, RESULT, "root", "element"));
      put(String.format(QUERY_RESULT_ROOT_NS_ENABLED_TEMPLATE, name), getString(query, RESULT, "root", "ns-enabled"));
      String schema = getString(query, RESULT, SCHEMA);
      put(String.format(QUERY_RESULT_SCHEMA_TEMPLATE, name), schema);
      put(String.format(QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, name), getString(query, XDBPDI, "entity", "path"));
      put(String.format(QUERY_XDBPDI_SCHEMA_TEMPLATE, name), getString(query, XDBPDI, SCHEMA));
      put(String.format(QUERY_XDBPDI_TEMPLATE_TEMPLATE, name), getString(query, XDBPDI, "template"));
      put(String.format(QUERY_XDBPDI_OPERAND_NAME, name, schema), getString(query, XDBPDI, OPERAND, NAME));
      put(String.format(QUERY_XDBPDI_OPERAND_PATH, name, schema), getString(query, XDBPDI, OPERAND, "path"));
      put(String.format(QUERY_XDBPDI_OPERAND_TYPE, name, schema), getString(query, XDBPDI, OPERAND, TYPE));
      put(String.format(QUERY_XDBPDI_OPERAND_INDEX, name, schema), getString(query, XDBPDI, OPERAND, "index"));
    }

    for (Map<String, Object> resultHelper: getList(source, "result-helper")) {
      String name = getString(resultHelper, NAME);
      append(RESULT_HELPER_NAME, name);
      put(String.format(RESULT_HELPER_SCHEMA_TEMPLATE, name), getString(resultHelper, SCHEMA));
      put(String.format(RESULT_HELPER_XML, name), getString(resultHelper, XML));
    }

    for (Map<String, Object> search: getList(source, "search")) {
      String searchName = getString(search, NAME);
      append(SEARCH_NAME, searchName);
      put(String.format(SEARCH_DESCRIPTION, searchName), getString(search, DESCRIPTION));
      put(String.format(SEARCH_NESTED, searchName), getString(search, "nested"));
      put(String.format(SEARCH_STATE, searchName), getString(search, "state"));
      put(String.format(SEARCH_INUSE, searchName), getString(search, "inuse"));
      put(String.format(SEARCH_AIC, searchName), getString(search, "aic"));
      put(String.format(SEARCH_QUERY, searchName), getString(search, "query"));

      for (Map<String, Object> composition: getList(search, "composition")) {
        String compositionName = getString(composition, NAME);
        append(SEARCH_COMPOSITION_NAME, compositionName);
        put(String.format(SEARCH_COMPOSITION_XFORM_NAME, searchName), getString(composition, "xform", NAME));
        put(String.format(SEARCH_COMPOSITION_XFORM, searchName), getString(composition, "xform", XML));
        put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME, searchName, compositionName), getString(composition, RESULT, MAIN, NAME));
        put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL, searchName, compositionName), getString(composition, RESULT, MAIN, "label"));
        put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH, searchName, compositionName), getString(composition, RESULT, MAIN, "path"));
        put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE, searchName, compositionName), getString(composition, RESULT, MAIN, TYPE));
        put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT, searchName, compositionName), getString(composition, RESULT, MAIN, "sort"));
        put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_ENABLED_TEMPLATE, searchName), getString(composition, "export", "enabled"));
        put(String.format(SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_CONFIG_TEMPLATE, searchName), getString(composition, "export", "configs"));
      }
    }
  }

  private void filter(String key, Map<String, Object> source, String... vars) {
    String value = getString(source, vars);
    if (value != null && !value.isEmpty()) {
      put(key, value);
    }
  }

  private void append(String name, String newValue) {
    String value = get(name);
    if (value == null) {
      put(name, newValue);
    } else {
      put(name, value + "," + newValue);
    }
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
