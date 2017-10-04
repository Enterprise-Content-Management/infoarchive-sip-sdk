/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.atteo.evo.inflector.English;

import com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.YamlMap;


class YamlPropertiesMap extends HashMap<String, String> implements InfoArchiveConfigurationProperties {

  private static final long serialVersionUID = 3429860978620277558L;
  private static final String NAMESPACE = "namespace";
  private static final String NAMESPACES = English.plural(NAMESPACE);
  private static final String CONTENT = "content";
  private static final String TEXT = "text";
  private static final String PATH = "path";
  private static final String LABEL = "label";

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
        FEDERATION_BOOTSTRAP, "bootstrap",
        FEDERATION_SUPERUSER_PASSWORD, "superUserPassword");
    putFrom("xdbDatabase",
        DATABASE_NAME, NAME,
        DATABASE_ADMIN_PASSWORD, "adminPassword");
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
        STORE_STORETYPE, "storeType",
        STORE_FOLDER, "fileSystemFolder",
        STORE_TYPE, TYPE);
    putFrom("aic",
        AIC_NAME, NAME);
    putFrom(yaml.get("aics", 0, "criteria").toMap(),
        CRITERIA_NAME, NAME,
        CRITERIA_LABEL, LABEL,
        CRITERIA_TYPE, TYPE,
        CRITERIA_PKEYMINATTR, "pkeyminattr",
        CRITERIA_PKEYMAXATTR, "pkeymaxattr",
        CRITERIA_PKEYVALUESATTR, "pkeyvaluesattr",
        CRITERIA_INDEXED, "indexed");
    putFrom("queryQuota",
        QUOTA_NAME, NAME,
        QUOTA_AIU, "aiu",
        QUOTA_AIP, "aip",
        QUOTA_DIP, "dip");
    putFrom("retentionPolicy",
        RETENTION_POLICY_NAME, NAME);
    putFrom("pdiSchema",
        PDI_SCHEMA_NAME, NAME);
    putContentFrom(PDI_SCHEMA, "pdiSchema");
    putContentFrom(PDI_XML, "pdi");
    putContentFrom(INGEST_XML, "ingest");

    putManyFrom("exportConfiguration", EXPORT_CONFIG_NAME, (name, exportConfiguration) -> {
      putTemplatedFrom(exportConfiguration, name, EXPORT_CONFIG_TYPE_TEMPLATE, "exportType",
          EXPORT_CONFIG_OPTIONS_TEMPLATE_XSL_RESULTFORMAT_TEMPLATE, "xslResultFormat",
          EXPORT_CONFIG_PIPELINE_TEMPLATE, "pipeline");
      putValueList(exportConfiguration, "options",
          String.format(EXPORT_CONFIG_OPTIONS_TEMPLATE_NAME, name),
          EXPORT_CONFIG_OPTIONS_TEMPLATE_VALUE_TEMPLATE, name);
      putValueList(exportConfiguration, "encryptedOptions",
          String.format(EXPORT_CONFIG_ENCRYPTED_OPTIONS_TEMPLATE_NAME, name),
          EXPORT_CONFIG_ENCRYPTED_OPTIONS_TEMPLATE_VALUE_TEMPLATE, name);

      YamlMap transformation = exportConfiguration.get("transformation").toMap();
      Value transformationName = transformation.get(NAME);
      appendTemplated(transformationName, EXPORT_CONFIG_TRANSFORMATIONS_TEMPLATE_NAME, name);
      putTemplated(transformation.get("portName"), EXPORT_CONFIG_TRANSFORMATIONS_TEMPLATE_PORTNAME_TEMPLATE,
          name, transformationName);
      putTemplated(transformation.get(NAME), EXPORT_CONFIG_TRANSFORMATIONS_TEMPLATE_TRANSFORMATION_TEMPLATE,
          name, transformationName);
    });

    putManyFrom("exportPipeline", EXPORT_PIPELINE_NAME, (name, exportPipeline) -> {
      putTemplatedFrom(exportPipeline, name, EXPORT_PIPELINE_COLLECTION_BASED_TEMPLATE, "collectionBased",
          EXPORT_PIPELINE_COMPOSITE_TEMPLATE, "composite",
          EXPORT_PIPELINE_DESCRIPTION_TEMPLATE, "description",
          EXPORT_PIPELINE_ENVELOPE_FORMAT_TEMPLATE, "envelopeFormat",
          EXPORT_PIPELINE_INCLUDES_CONTENT_TEMPLATE, "includesContent",
          EXPORT_PIPELINE_INPUT_FORMAT_TEMPLATE, "inputFormat",
          EXPORT_PIPELINE_OUTPUT_FORMAT_TEMPLATE, "outputFormat",
          EXPORT_PIPELINE_TYPE_TEMPLATE, TYPE);
      putContentFrom(String.format(EXPORT_PIPELINE_CONTENT_TEMPLATE, name), exportPipeline, CONTENT);
    });

    putManyFrom("exportTransformation", EXPORT_TRANSFORMATION_NAME, (name, exportTransformation) ->
      putTemplatedFrom(exportTransformation, name, EXPORT_TRANSFORMATION_DESCRIPTION_TEMPLATE, "description",
          EXPORT_TRANSFORMATION_MAIN_PATH_TEMPLATE, "mainPath",
          EXPORT_TRANSFORMATION_TYPE_TEMPLATE, TYPE)
    );

    putManyFrom("resultConfigurationHelper", RESULT_HELPER_NAME, (name, resultConfigurationHelper) -> {
      putTemplated(resultConfigurationHelper.get(CONTENT, TEXT), RESULT_HELPER_XML, name);
      Value prefix = resultConfigurationHelper.get(NAMESPACES).toList().get(0);
      Value namespaceUri = lookupNamespace("prefix", prefix, "uri");
      putTemplated(namespaceUri, RESULT_HELPER_SCHEMA_TEMPLATE, name);
    });

    putManyFrom("query", QUERY_NAME, (name, query) -> {
      putTemplatedFrom(query, name, QUERY_NAMESPACE_URI_TEMPLATE, NAMESPACE,
          QUERY_RESULT_ROOT_ELEMENT_TEMPLATE, "resultRootElement",
          QUERY_RESULT_ROOT_NS_ENABLED_TEMPLATE, "resultRootNsEnabled");
      Map<Value, Value> namespaceUriByPrefix = new LinkedHashMap<>();
      query.get(NAMESPACES).toList().stream()
          .forEach(prefix -> {
            Value namespaceUri = lookupNamespace("prefix", prefix, "uri");
            namespaceUriByPrefix.put(prefix, namespaceUri);
            appendTemplated(prefix, QUERY_NAMESPACE_PREFIX_TEMPLATE, name);
            appendTemplated(namespaceUri, QUERY_NAMESPACE_URI_TEMPLATE, name);
          });
      putTemplated(namespaceUriByPrefix.values().iterator().next(), QUERY_RESULT_SCHEMA_TEMPLATE, name);
      YamlMap xdbPdiConfigs = query.get("xdbPdiConfigs").toMap();
      putTemplatedFrom(xdbPdiConfigs, name, QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, "entityPath",
          QUERY_XDBPDI_TEMPLATE_TEMPLATE, "template");
      Value prefix = xdbPdiConfigs.get(NAMESPACES, 0);
      Value namespaceUri = namespaceUriByPrefix.getOrDefault(prefix, new Value());
      putTemplated(namespaceUri, QUERY_XDBPDI_SCHEMA_TEMPLATE, name);
      xdbPdiConfigs.get("operands").toList().stream()
          .map(Value::toMap)
          .forEach(operand -> {
            appendTemplated(operand.get(NAME), QUERY_XDBPDI_OPERAND_NAME, name, namespaceUri);
            appendTemplated(operand.get("index"), QUERY_XDBPDI_OPERAND_INDEX, name, namespaceUri);
            appendTemplated(operand.get(TYPE), QUERY_XDBPDI_OPERAND_TYPE, name, namespaceUri);
            appendTemplated(operand.get(PATH), QUERY_XDBPDI_OPERAND_PATH, name, namespaceUri);
          });
    });

    List<Value> aics = yaml.get("aics").toList();
    if (!aics.isEmpty()) {
      YamlMap aic = aics.get(0).toMap();
      put(AIC_NAME, aic.get(NAME));
      aic.get("criteria").toList().stream()
          .map(Value::toMap)
          .forEach(criterion -> {
            append(CRITERIA_NAME, criterion.get(NAME));
            append(CRITERIA_LABEL, criterion.get(LABEL));
            append(CRITERIA_TYPE, criterion.get(TYPE));
            append(CRITERIA_INDEXED, criterion.get("indexed"));
            append(CRITERIA_PKEYMINATTR, criterion.get("pkeyMinAttr"));
            append(CRITERIA_PKEYMAXATTR, criterion.get("pkeyMaxAttr"));
            append(CRITERIA_PKEYVALUESATTR, criterion.get("pkeyValuesAttr"));
          });
    }

    putManyFrom("search", SEARCH_NAME, (searchName, search) -> {
      putTemplatedFrom(search, searchName, SEARCH_DESCRIPTION, DESCRIPTION,
          SEARCH_NESTED, "nestedSearch",
          SEARCH_STATE, "state",
          SEARCH_AIC, "aic",
          SEARCH_QUERY, "query");
      with("searchComposition", "search", searchName, searchComposition -> {
        Value searchCompositionName = searchComposition.get(NAME);
        putTemplated(searchCompositionName, SEARCH_COMPOSITION_NAME, searchName);
        with("resultMaster", NAME, yaml.get("resultMasters", 0, NAME).toString(), resultMaster -> {
          YamlMap tab = resultMaster.get("panels", 0, "tabs", 0).toMap();
          putTemplated(tab.get("exportEnabled"), SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_ENABLED_TEMPLATE, searchName);
          tab.get("columns").toList().stream()
              .map(Value::toMap)
              .forEach(column -> {
                appendTemplated(column.get(NAME), SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_NAME, searchName,
                    searchCompositionName);
                appendTemplated(column.get(LABEL), SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_LABEL, searchName,
                    searchCompositionName);
                appendTemplated(column.get(PATH), SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_PATH, searchName,
                    searchCompositionName);
                appendTemplated(column.get("defaultSort"), SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_SORT, searchName,
                    searchCompositionName);
                appendTemplated(column.get("dataType"), SEARCH_COMPOSITION_RESULT_MAIN_COLUMN_TYPE, searchName,
                    searchCompositionName);
              });
          tab.get("exportConfigurations").toList().stream()
              .forEach(exportConfiguration ->
                appendTemplated(exportConfiguration, SEARCH_COMPOSITION_RESULT_MAIN_EXPORT_CONFIG_TEMPLATE, searchName)
              );
        });
        with("xform", NAME, yaml.get("xforms", 0, NAME).toString(), xform -> {
          putTemplated(xform.get(NAME), SEARCH_COMPOSITION_XFORM_NAME, searchName);
          putTemplated(xform.get(CONTENT, TEXT), SEARCH_COMPOSITION_XFORM, searchName);
        });
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
      put(destination, map.get(source));
    }
  }

  private void put(String key, Value value) {
    if (!value.isEmpty()) {
      put(key, value.toString());
    }
  }

  private void putContentFrom(String property, String type) {
    putContentFrom(property, yaml, English.plural(type), 0, CONTENT);
  }

  private void putContentFrom(String property, YamlMap map, Object... args) {
    putFrom(map.get(args).toMap(), property, TEXT);
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

  private void append(String name, Value value) {
    append(name, value.toString());
  }

  private void append(String name, String value) {
    String oldValue = get(name);
    if (oldValue == null) {
      put(name, value);
    } else {
      put(name, oldValue + "," + value);
    }
  }

  private void appendTemplated(Value value, String format, Object... args) {
    append(String.format(format, args), value.toString());
  }

  private void putTemplatedFrom(YamlMap map, String name, String... destinationAndSourceNames) {
    int i = 0;
    while (i < destinationAndSourceNames.length) {
      String destination = destinationAndSourceNames[i++];
      String source = destinationAndSourceNames[i++];
      putTemplated(map.get(source), destination, name);
    }
  }

  private void putTemplated(Value value, String template, Object... args) {
    put(String.format(template, args), value);
  }

  private Value lookupNamespace(String lookupProperty, Value lookupValue, String returnProperty) {
    return lookup(NAMESPACE, lookupProperty, lookupValue, returnProperty);
  }

  private Value lookup(String type, String lookupProperty, Value lookupValue, String returnProperty) {
    return lookup(type, lookupProperty, lookupValue.toString())
        .map(map -> map.get(returnProperty))
        .orElse(new Value());
  }

  private Optional<YamlMap> lookup(String type, String lookupProperty, String lookupValue) {
    return yaml.get(English.plural(type)).toList().stream()
        .map(Value::toMap)
        .filter(sc -> sc.get(lookupProperty).toString().equals(lookupValue))
        .findAny();
  }

  private void with(String type, String lookupProperty, String lookupValue, Consumer<YamlMap> action) {
    lookup(type, lookupProperty, lookupValue).ifPresent(action);
  }

  private void putValueList(YamlMap map, String valueListProperty, String valueListName, String format,
      String name) {
    map.get(valueListProperty).toMap().entries().forEach(e -> {
      append(valueListName, e.getKey());
      putTemplated(e.getValue(), format, name, e.getKey());
    });
  }

}
