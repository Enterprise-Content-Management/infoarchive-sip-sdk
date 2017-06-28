/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.*;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.PropertyVisitor;
import com.opentext.ia.yaml.core.Value;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class InsertDefaultReferences extends PropertyVisitor {

  private static final String NAME = "name";
  private static final String DEFAULT = "default";
  private static final String TENANT = "tenant";
  private static final String APPLICATION = "application";
  private static final String SPACE = "space";
  private static final String XDB_STORE = "xdbStore";
  private static final String SEARCH = "search";
  private static final String SEARCH_COMPOSITION = "searchComposition";
  private static final String NAMESPACE = "namespace";
  private static final Map<String, Collection<String>> REFERENCE_PROPERTIES_BY_PATH_REGEX
      = referencePropertiesByPathRegex();
  private static final Map<String, String> TYPE_BY_REFERENCE_PROPERTY = typeByReferenceProperty();

  private static Map<String, Collection<String>> referencePropertiesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/.*/content", Arrays.asList(APPLICATION, "store"));
    result.put("/.+/((.+\\.)?q|.+Q)uery", Arrays.asList(NAMESPACE));
    result.put("/aics/\\d+", Arrays.asList(APPLICATION));
    result.put("/aips/\\d+", Arrays.asList(APPLICATION, "xdbLibrary"));
    result.put("/applicationCategories/\\d+", Arrays.asList(TENANT));
    result.put("/applications/\\d+", Arrays.asList(TENANT));
    result.put("/buckets/\\d+", Arrays.asList("spaceRootObject"));
    result.put("/confirmations/\\d+", Arrays.asList(APPLICATION, "deliveryChannel"));
    result.put("/customPresentationConfigurations/\\d+", Arrays.asList("exportPipeline", TENANT));
    result.put("/databases/\\d+", Arrays.asList(APPLICATION, "ciStore", "managedItemStore", XDB_STORE));
    result.put("/databaseCryptoes/\\d+", Arrays.asList(APPLICATION, "database"));
    result.put("/deliveryChannels/\\d+", Arrays.asList(APPLICATION, "store"));
    result.put("/exportConfigurations/\\d+", Arrays.asList("pipeline", TENANT, "transformation"));
    result.put("/exportPipelines/\\d+", Arrays.asList(APPLICATION, TENANT));
    result.put("/exportTransformations/\\d+", Arrays.asList(APPLICATION, TENANT));
    result.put("/fileSystemFolders/\\d+", Arrays.asList("spaceRootFolder"));
    result.put("/holdings/\\d+", Arrays.asList(APPLICATION, "ciStore", "ingest", "logStore", "managedItemStore", "pdi",
        "renditionStore", "sipStore", "stagingStore", "xdbLibrary", "xdbLibraryPolicy", XDB_STORE, "xmlStore"));
    result.put("/holdingCryptoes/\\d+", Arrays.asList(APPLICATION, "cryptoObject", "holding", "pdiCrypto"));
    result.put("/ingests/\\d+", Arrays.asList(APPLICATION));
    result.put("/ingestNodes/\\d+", Arrays.asList(APPLICATION));
    result.put("/orders/\\d+", Arrays.asList(APPLICATION));
    result.put("/pdis/\\d+", Arrays.asList(APPLICATION));
    result.put("/pdis/\\d+/context/text/data/\\d+/indexes", Arrays.asList(NAMESPACE));
    result.put("/pdiCryptoes/\\d+", Arrays.asList(APPLICATION));
    result.put("/pdiSchemas/\\d+", Arrays.asList(APPLICATION, NAMESPACE));
    result.put("/queries/\\d+", Arrays.asList(APPLICATION, NAMESPACE, "order", "queryQuota"));
    result.put("/queries/\\d+/xdbPdiConfigs", Arrays.asList(NAMESPACE));
    result.put("/queryQuotas/\\d+", Arrays.asList(APPLICATION));
    result.put("/receiverNodes/\\d+", Arrays.asList(APPLICATION));
    result.put("/resultConfigurationHelpers/\\d+", Arrays.asList(APPLICATION, NAMESPACE));
    result.put("/resultMasters/\\d+", Arrays.asList(SEARCH, SEARCH_COMPOSITION));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+", Arrays.asList("exportConfiguration"));
    result.put("/schemas/\\d+", Arrays.asList("database", XDB_STORE));
    result.put("/searches/\\d+", Arrays.asList("aic", APPLICATION, "searchGroup", "query"));
    result.put("/searchGroups/\\d+", Arrays.asList(APPLICATION));
    result.put("/searchCompositions/\\d+", Arrays.asList("resultMaster", SEARCH, "xform"));
    result.put("/spaces/\\d+", Arrays.asList(APPLICATION));
    result.put("/spaceRootFolders/\\d+", Arrays.asList("fileSystemRoot", SPACE));
    result.put("/spaceRootObjects/\\d+", Arrays.asList(SPACE));
    result.put("/spaceRootXdbLibraries/\\d+", Arrays.asList(SPACE, "xdbDatabase"));
    result.put("/stores/\\d+", Arrays.asList(APPLICATION, "fileSystemFolder"));
    result.put("/storageEndPointCredentials/\\d+", Arrays.asList("storageEndPoint"));
    result.put("/tables/\\d+", Arrays.asList(APPLICATION, "schema"));
    result.put("/tranformations/\\d+", Arrays.asList(APPLICATION));
    result.put("/valueLists/\\d+", Arrays.asList(APPLICATION));
    result.put("/xdbDatabases/\\d+", Arrays.asList("xdbFederation"));
    result.put("/xdbLibraries/\\d+", Arrays.asList(APPLICATION, "spaceRootXdbLibrary"));
    result.put("/xdbLibraryPolicies/\\d+", Arrays.asList(APPLICATION));
    result.put("/xforms/\\d+", Arrays.asList(SEARCH, SEARCH_COMPOSITION));
    result.put("/xqueries/\\d+", Arrays.asList(SEARCH, SEARCH_COMPOSITION));
    result.put("/xqueryModules/\\d+", Arrays.asList(APPLICATION, TENANT));
    return result;
  }

  private static Map<String, String> typeByReferenceProperty() {
    Map<String, String> result = new HashMap<>();
    result.put("pipeline", "exportPipeline");
    result.put("transformation", "exportTransformation");
    return result;
  }

  InsertDefaultReferences() {
    super(REFERENCE_PROPERTIES_BY_PATH_REGEX);
  }

  @Override
  protected void visitProperty(Visit visit, String property) {
    YamlMap yaml = visit.getMap();
    if (missesProperty(yaml, property)) {
      getDefaultValueFor(visit, typeOf(property))
          .ifPresent(defaultValue -> yaml.put(property, defaultValue));
    }
  }

  private boolean missesProperty(YamlMap yaml, String property) {
    if (yaml.containsKey(property)) {
      return false;
    }
    return !NAMESPACE.equals(property) || !yaml.containsKey(English.plural(NAMESPACE));
  }

  private String typeOf(String property) {
    return TYPE_BY_REFERENCE_PROPERTY.getOrDefault(property, property);
  }

  private Optional<String> getDefaultValueFor(Visit visit, String type) {
    return getDefaultInstance(visit, English.plural(type), idPropertyFor(type));
  }

  private String idPropertyFor(String type) {
    return NAMESPACE.equals(type) ? "prefix" : NAME;
  }

  private Optional<String> getDefaultInstance(Visit visit, String collection, String idProperty) {
    List<Value> instances = visit.getRootMap().get(collection).toList();
    if (instances.size() == 1) {
      return Optional.of(instances.get(0).toMap().get(idProperty).toString());
    }
    return instances.stream()
        .map(Value::toMap)
        .filter(map -> map.get(DEFAULT).toBoolean())
        .map(map -> map.get(idProperty).toString())
        .findAny();
  }

}
