/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.HashMap;
import java.util.function.Consumer;

import com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties;
import com.opentext.ia.sdk.support.yaml.Value;
import com.opentext.ia.sdk.support.yaml.YamlMap;


class YamlPropertiesMap extends HashMap<String, String> implements InfoArchiveConfigurationProperties {

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


  YamlPropertiesMap(YamlMap yaml) {
    flatten(yaml);
  }

  @SuppressWarnings("PMD.UnusedFormalParameter")
  private void flatten(YamlMap yaml) {
    put(SERVER_URI, getString(yaml, SERVER, "uri"));

    filter(SERVER_AUTENTICATION_TOKEN, yaml, SERVER, AUTHENTICATION, "token");
    filter(SERVER_AUTHENTICATION_USER, yaml, SERVER, AUTHENTICATION, "user");
    filter(SERVER_AUTHENTICATION_PASSWORD, yaml, SERVER, AUTHENTICATION, "password");
    filter(SERVER_AUTHENTICATION_GATEWAY, yaml, SERVER, AUTHENTICATION, "gateway");
    filter(SERVER_CLIENT_ID, yaml, SERVER, AUTHENTICATION, "client_id");
    filter(SERVER_CLIENT_SECRET, yaml, SERVER, AUTHENTICATION, "client_secret");

    put(TENANT_NAME, getString(yaml, "tenant"));

    put(FEDERATION_NAME, getString(yaml, XDB, FEDERATION, NAME));
    put(FEDERATION_BOOTSTRAP, getString(yaml, XDB, FEDERATION, "uri"));
    put(FEDERATION_SUPERUSER_PASSWORD, getString(yaml, XDB, FEDERATION, "password"));
    put(DATABASE_NAME, getString(yaml, XDB, "database", NAME));
    put(DATABASE_ADMIN_PASSWORD, getString(yaml, XDB, "database", "password"));

    put(APPLICATION_NAME, getString(yaml, APPLICATION, NAME));
    put(APPLICATION_CATEGORY, getString(yaml, APPLICATION, "category"));
    put(APPLICATION_DESCRIPTION, getString(yaml, APPLICATION, DESCRIPTION));

    put(HOLDING_NAME, getString(yaml, "holding", NAME));

    filter(FILE_SYSTEM_FOLDER, yaml, "file-system-folder", NAME);

    filter(STORE_NAME, yaml, STORE, NAME);
    filter(STORE_STORETYPE, yaml, STORE, "store-type");
    filter(STORE_FOLDER, yaml, STORE, "folder");
    filter(STORE_TYPE, yaml, STORE, TYPE);

    put(AIC_NAME, getString(yaml, AIC, NAME));
    put(CRITERIA_NAME, getString(yaml, AIC, CRITERIA, NAME));
    put(CRITERIA_LABEL, getString(yaml, AIC, CRITERIA, "label"));
    put(CRITERIA_TYPE, getString(yaml, AIC, CRITERIA, TYPE));
    put(CRITERIA_PKEYMINATTR, getString(yaml, AIC, CRITERIA, "pkeyminattr"));
    put(CRITERIA_PKEYMAXATTR, getString(yaml, AIC, CRITERIA, "pkeymaxattr"));
    put(CRITERIA_PKEYVALUESATTR, getString(yaml, AIC, CRITERIA, "pkeyvaluesattr"));
    put(CRITERIA_INDEXED, getString(yaml, AIC, CRITERIA, "indexed"));

    put(QUOTA_NAME, getString(yaml, QUOTA, NAME));
    put(QUOTA_AIU, getString(yaml, QUOTA, "aiu"));
    put(QUOTA_AIP, getString(yaml, QUOTA, "aip"));
    put(QUOTA_DIP, getString(yaml, QUOTA, "dip"));

    put(RETENTION_POLICY_NAME, getString(yaml, "retention-policy", NAME));

    put(PDI_SCHEMA_NAME, getString(yaml, "pdiSchema", "namespace"));
    put(PDI_SCHEMA, getString(yaml, "pdiSchema", "content", "text"));
    put(PDI_XML, getString(yaml, "pdi", "content", "text"));
    put(INGEST_XML, getString(yaml, "ingest", "content", "text"));

    forEachMapItem(yaml, "query", query -> {
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
    });

    forEachMapItem(yaml, "result-helper", resultHelper -> {
      String name = getString(resultHelper, NAME);
      append(RESULT_HELPER_NAME, name);
      put(String.format(RESULT_HELPER_SCHEMA_TEMPLATE, name), getString(resultHelper, SCHEMA));
      put(String.format(RESULT_HELPER_XML, name), getString(resultHelper, XML));
    });

    forEachMapItem(yaml, "search", search -> {
      String searchName = getString(search, NAME);
      append(SEARCH_NAME, searchName);
      put(String.format(SEARCH_DESCRIPTION, searchName), getString(search, DESCRIPTION));
      put(String.format(SEARCH_NESTED, searchName), getString(search, "nested"));
      put(String.format(SEARCH_STATE, searchName), getString(search, "state"));
      put(String.format(SEARCH_INUSE, searchName), getString(search, "inuse"));
      put(String.format(SEARCH_AIC, searchName), getString(search, "aic"));
      put(String.format(SEARCH_QUERY, searchName), getString(search, "query"));

      forEachMapItem(search, "composition", composition -> {
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
      });
    });
  }

  private void forEachMapItem(YamlMap yaml, String name, Consumer<YamlMap> action) {
    Value value = yaml.get(name);
    if (value.isMap()) {
      action.accept(value.toMap());
    } else {
      value.toList().stream()
          .map(item -> item.toMap())
          .forEach(action);
    }
  }

  private String getString(YamlMap yaml, Object... names) {
    return yaml.get(names).toString();
  }

  private void filter(String key, YamlMap yaml, Object... names) {
    String value = getString(yaml, names);
    if (!value.isEmpty()) {
      put(key, value);
    }
  }

  private void append(String name, String value) {
    String oldValue = get(name);
    if (oldValue == null) {
      put(name, value);
    } else {
      put(name, oldValue + "," + value);
    }
  }

}
