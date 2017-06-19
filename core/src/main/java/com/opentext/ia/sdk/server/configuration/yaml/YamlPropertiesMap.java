/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.atteo.evo.inflector.English;

import com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties;
import com.opentext.ia.sdk.yaml.core.Value;
import com.opentext.ia.sdk.yaml.core.YamlMap;


class YamlPropertiesMap extends HashMap<String, String> implements InfoArchiveConfigurationProperties {

  private static final long serialVersionUID = 3429860978620277558L;
  private static final String NAMESPACE = "namespace";
  private static final String SCHEMA = "schema";
  private static final String RESULT = "result";
  private static final String XDBPDI = "xdbpdi";
  private static final String OPERAND = "operand";
  private static final String MAIN = "main";

  private final transient YamlMap yaml;


  YamlPropertiesMap(YamlMap yaml) {
    this.yaml = yaml;
    flatten();
  }

  private void flatten() {
    putFrom("tenant",
        TENANT_NAME, NAME);
    putFrom("xdbFederation",
        FEDERATION_NAME, NAME,
        FEDERATION_BOOTSTRAP, "uri",
        FEDERATION_SUPERUSER_PASSWORD, "password");
    putFrom("xdbDatabase",
        DATABASE_NAME, NAME,
        DATABASE_ADMIN_PASSWORD, "password");
    putFrom("application",
        APPLICATION_NAME, NAME,
        APPLICATION_CATEGORY, "category",
        APPLICATION_DESCRIPTION, DESCRIPTION);
    putFrom("holding",
        HOLDING_NAME, NAME);
    putFrom("fileSystemFolder",
        FILE_SYSTEM_FOLDER, NAME);
    putFrom("store",
        STORE_NAME, NAME,
        STORE_STORETYPE, "store-type",
        STORE_FOLDER, "folder",
        STORE_TYPE, TYPE);
    putFrom("aic",
        AIC_NAME, NAME);
    putFrom(yaml.get("aics", 0, "criteria").toMap(),
        CRITERIA_NAME, NAME,
        CRITERIA_LABEL, "label",
        CRITERIA_TYPE, TYPE,
        CRITERIA_PKEYMINATTR, "pkeyminattr",
        CRITERIA_PKEYMAXATTR, "pkeymaxattr",
        CRITERIA_PKEYVALUESATTR, "pkeyvaluesattr",
        CRITERIA_INDEXED, "indexed");
    putFrom("quota",
        QUOTA_NAME, NAME,
        QUOTA_AIU, "aiu",
        QUOTA_AIP, "aip",
        QUOTA_DIP, "dip");
    putFrom("retentionPolicy",
        RETENTION_POLICY_NAME, NAME);
    putFrom("pdiSchema",
        PDI_SCHEMA_NAME, NAMESPACE);
    putContentFrom("pdiSchema", PDI_SCHEMA);
    putContentFrom("pdi", PDI_XML);
    putContentFrom("ingest", INGEST_XML);

    putManyFrom("query", QUERY_NAME, (name, map) -> {
      putTemplatedFrom(map, name, QUERY_NAMESPACE_URI_TEMPLATE, NAMESPACE,
          QUERY_RESULT_ROOT_ELEMENT_TEMPLATE, "resultRootElement",
          QUERY_RESULT_ROOT_NS_ENABLED_TEMPLATE, "resultRootNsEnabled");
      putTemplated(QUERY_NAMESPACE_PREFIX_TEMPLATE, name, lookup(NAMESPACE, "uri", map.get(NAMESPACE), "prefix"));
    });

    // TODO: Fix the code below
    forEachMapItem(yaml, "query", query -> {
      String name = getString(query, NAME);
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
      put(String.format(RESULT_HELPER_XML, name), getString(resultHelper, "xml"));
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
        put(String.format(SEARCH_COMPOSITION_XFORM, searchName), getString(composition, "xform", "xml"));
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

  private void putFrom(String type, String... destinationAndSourceNames) {
    putFrom(yaml.get(English.plural(type), 0).toMap(), destinationAndSourceNames);
  }

  private void putFrom(YamlMap map, String... destinationAndSourceNames) {
    int i = 0;
    while (i < destinationAndSourceNames.length) {
      String destination = destinationAndSourceNames[i++];
      String source = destinationAndSourceNames[i++];
      put(destination, map.get(source).toString());
    }
  }

  private void putContentFrom(String type, String property) {
    putFrom(yaml.get(English.plural(type), 0, "content").toMap(),
        property, "text");
  }

  private void putManyFrom(String type, String property, BiConsumer<String, YamlMap> consumer) {
    putManyFrom(yaml.get(English.plural(type)), property, consumer);
  }

  private void putManyFrom(Value value, String property, BiConsumer<String, YamlMap> consumer) {
    value.toList().stream()
        .map(Value::toMap)
        .forEach(map -> {
          String name = map.get(NAME).toString();
          append(property, name);
          consumer.accept(name, map);
        });
  }

  private void append(String name, String value) {
    String oldValue = get(name);
    if (oldValue == null) {
      put(name, value);
    } else {
      put(name, oldValue + "," + value);
    }
  }

  private void putTemplatedFrom(YamlMap map, String name, String... destinationAndSourceNames) {
    int i = 0;
    while (i < destinationAndSourceNames.length) {
      String destination = destinationAndSourceNames[i++];
      String source = destinationAndSourceNames[i++];
      putTemplated(destination, name, map.get(source));
    }
  }

  private String putTemplated(String template, String name, Value value) {
    return put(String.format(template, name), value.toString());
  }

  private Value lookup(String type, String lookupProperty, Value lookupValue, String resultProperty) {
    return yaml.get(English.plural(type)).toList().stream()
        .map(Value::toMap)
        .filter(map -> lookupValue.equals(map.get(lookupProperty)))
        .map(map -> map.get(resultProperty))
        .findAny()
        .orElse(new Value());
  }

  private void forEachMapItem(YamlMap map, String name, Consumer<YamlMap> action) {
    Value value = map.get(name);
    if (value.isMap()) {
      action.accept(value.toMap());
    } else {
      value.toList().stream()
          .map(Value::toMap)
          .forEach(action);
    }
  }

  private String getString(YamlMap map, Object... names) {
    return map.get(names).toString();
  }

}
