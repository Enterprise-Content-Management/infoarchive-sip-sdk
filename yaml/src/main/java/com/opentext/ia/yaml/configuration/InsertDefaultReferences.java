/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.atteo.evo.inflector.English;

import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;


class InsertDefaultReferences extends BaseInsertDefaultReferences {

  private static final String NAME = "name";
  private static final String NAMESPACE = "namespace";
  private static final String TENANT = "tenant";
  private static final String APPLICATION = "application";
  private static final String SPACE = "space";
  private static final String STORE = "store";
  private static final String CI_STORE = "ciStore";
  private static final String MANAGED_ITEM_STORE = "managedItemStore";
  private static final String XDB_STORE = "xdbStore";
  private static final String SEARCH = "search";
  private static final String SEARCH_COMPOSITION = "searchComposition";
  private static final String EXPORT_PIPELINE = "exportPipeline";
  private static final Map<String, Collection<String>> REFERENCE_PROPERTIES_BY_PATH_REGEX
      = referencePropertiesByPathRegex();
  private static final Map<String, String> TYPE_BY_REFERENCE_PROPERTY = typeByReferenceProperty();
  private static final Collection<String> PSEUDO_CONTENT
      = Arrays.asList(EXPORT_PIPELINE, "exportTransformation", "valueList");

  private static Map<String, Collection<String>> referencePropertiesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/.*/content", Arrays.asList(APPLICATION, STORE));
    result.put("/.+/((.+\\.)?q|.+Q)uery", Arrays.asList(NAMESPACE));
    result.put("/aics/\\d+", Arrays.asList(APPLICATION));
    result.put("/aips/\\d+", Arrays.asList(APPLICATION, "xdbLibrary"));
    result.put("/applicationCategories/\\d+", Arrays.asList(TENANT));
    result.put("/applications/\\d+", Arrays.asList(TENANT));
    result.put("/buckets/\\d+", Arrays.asList("spaceRootObject"));
    result.put("/confirmations/\\d+", Arrays.asList(APPLICATION, "deliveryChannel"));
    result.put("/customPresentationConfigurations/\\d+", Arrays.asList(EXPORT_PIPELINE));
    result.put("/databases/\\d+", Arrays.asList(APPLICATION, CI_STORE, MANAGED_ITEM_STORE, XDB_STORE));
    result.put("/databaseCryptoes/\\d+", Arrays.asList(APPLICATION, "database"));
    result.put("/deliveryChannels/\\d+", Arrays.asList(APPLICATION, STORE));
    result.put("/exportConfigurations/\\d+", Arrays.asList("pipeline", "transformation"));
    result.put("/holds/\\d+", Arrays.asList(TENANT));
    result.put("/holdings/\\d+", Arrays.asList(APPLICATION, CI_STORE, "ingest", "logStore", MANAGED_ITEM_STORE,
        "renditionStore", "sipStore", "stagingStore", "xdbLibraryPolicy", XDB_STORE, "xmlStore"));
    result.put("/holdings/\\d+/ingestConfigs/\\d+", Arrays.asList("ingest"));
    result.put("/holdings/\\d+/pdiConfigs/\\d+", Arrays.asList("pdi"));
    result.put("/holdingCryptoes/\\d+", Arrays.asList(APPLICATION, "holding"));
    result.put("/holdingCryptoes/\\d+/pdis/\\d+", Arrays.asList("pdiCrypto"));
    result.put("/ingests/\\d+", Arrays.asList(APPLICATION));
    result.put("/ingestNodes/\\d+", Arrays.asList(APPLICATION));
    result.put("/orders/\\d+", Arrays.asList(APPLICATION));
    result.put("/pdis/\\d+", Arrays.asList(APPLICATION));
    result.put("/pdis/\\d+/context/text/data/\\d+/indexes", Arrays.asList(NAMESPACE));
    result.put("/pdiCryptoes/\\d+", Arrays.asList(APPLICATION));
    result.put("/pdiSchemas/\\d+", Arrays.asList(APPLICATION, NAMESPACE));
    result.put("/queries/\\d+", Arrays.asList(APPLICATION, NAMESPACE, "order"));
    result.put("/queries/[^/]+/xdbPdiConfigs", Arrays.asList(NAMESPACE));
    result.put("/queryQuotas/\\d+", Arrays.asList(APPLICATION));
    result.put("/receiverNodes/\\d+", Arrays.asList(APPLICATION));
    result.put("/resultConfigurationHelpers/\\d+", Arrays.asList(APPLICATION, NAMESPACE));
    result.put("/resultMasters/\\d+", Arrays.asList(SEARCH, SEARCH_COMPOSITION));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+", Arrays.asList("exportConfiguration"));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+/customPresentation", Arrays.asList(APPLICATION, TENANT));
    result.put("/retentionPolicies/\\d+", Arrays.asList(TENANT));
    result.put("/rules/\\d+", Arrays.asList(APPLICATION));
    result.put("/schemas/\\d+", Arrays.asList("database", XDB_STORE));
    result.put("/searches/\\d+", Arrays.asList("aic", APPLICATION, "database", "query"));
    result.put("/searchGroups/\\d+", Arrays.asList(APPLICATION));
    result.put("/searchCompositions/\\d+", Arrays.asList(SEARCH));
    result.put("/spaces/\\d+", Arrays.asList(APPLICATION));
    result.put("/spaceRootFolders/\\d+", Arrays.asList("fileSystemRoot", SPACE));
    result.put("/spaceRootObjects/\\d+", Arrays.asList(SPACE, "customStorage", "storageEndPointCredential"));
    result.put("/spaceRootXdbLibraries/\\d+", Arrays.asList(SPACE, "xdbDatabase"));
    result.put("/stores/\\d+", Arrays.asList(APPLICATION, "bucket", "fileSystemFolder"));
    result.put("/storageEndPointCredentials/\\d+", Arrays.asList("storageEndPoint"));
    result.put("/tables/\\d+", Arrays.asList(APPLICATION, "schema"));
    result.put("/transformations/\\d+", Arrays.asList(APPLICATION));
    result.put("/valueLists/\\d+", Arrays.asList(APPLICATION));
    result.put("/xdbLibraries/\\d+", Arrays.asList(APPLICATION));
    result.put("/xdbLibraryPolicies/\\d+", Arrays.asList(APPLICATION));
    result.put("/xforms/\\d+", Arrays.asList(SEARCH, SEARCH_COMPOSITION));
    result.put("/xqueries/\\d+", Arrays.asList(SEARCH, SEARCH_COMPOSITION));
    return result;
  }

  private static Map<String, String> typeByReferenceProperty() {
    Map<String, String> result = new HashMap<>();
    result.put("pipeline", EXPORT_PIPELINE);
    result.put("transformation", "exportTransformation");
    result.put(CI_STORE, STORE);
    result.put(MANAGED_ITEM_STORE, STORE);
    result.put(XDB_STORE, STORE);
    return result;
  }

  InsertDefaultReferences() {
    super(REFERENCE_PROPERTIES_BY_PATH_REGEX);
  }

  @Override
  public boolean test(Visit visit) {
    if (!super.test(visit)) {
      return false;
    }
    if (!visit.getPath().endsWith("/content")) {
      return true;
    }
    return PSEUDO_CONTENT.stream()
        .map(type -> '/' + English.plural(type) + '/')
        .noneMatch(path -> visit.getPath().startsWith(path));
  }

  @Override
  protected boolean missesProperty(Visit visit, String property) {
    YamlMap yaml = visit.getMap();
    if (yaml.containsKey(property)) {
      return false;
    }
    if (NAMESPACE.equals(property)) {
      if (visit.getPath().startsWith("/pdiSchemas/") && yaml.containsKey(NAME)) {
        return false;
      }
      return !yaml.containsKey(English.plural(NAMESPACE));
    }
    return true;
  }

  @Override
  protected String typeOf(String property) {
    return TYPE_BY_REFERENCE_PROPERTY.getOrDefault(property, property);
  }

}
