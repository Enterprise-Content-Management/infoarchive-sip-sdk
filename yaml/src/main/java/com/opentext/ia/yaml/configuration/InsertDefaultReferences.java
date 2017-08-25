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

  private static final String STORE = "store";
  private static final String NAME = "name";
  private static final String NAMESPACE = "namespace";
  private static final String TENANT = "tenant";
  private static final String APPLICATION = "application";
  private static final String SPACE = "space";
  private static final String CI_STORE = "ciStore";
  private static final String MANAGED_ITEM_STORE = "managedItemStore";
  private static final String XDB_STORE = "xdbStore";
  private static final String SEARCH = "search";
  private static final String SEARCH_COMPOSITION = "searchComposition";
  private static final Map<String, Collection<String>> REFERENCE_PROPERTIES_BY_PATH_REGEX
      = referencePropertiesByPathRegex();
  private static final Map<String, String> TYPE_BY_REFERENCE_PROPERTY = typeByReferenceProperty();
  private static final Collection<String> PSEUDO_CONTENT
      = Arrays.asList("exportPipeline", "exportTransformation", "valueList");

  private static Map<String, Collection<String>> referencePropertiesByPathRegex() {
    Map<String, Collection<String>> result = new HashMap<>();
    result.put("/.*/content", Arrays.asList(APPLICATION, STORE));
    result.put("/.+/((.+\\.)?q|.+Q)uery", Arrays.asList(NAMESPACE));
    result.put("/aics/\\d+", Arrays.asList(APPLICATION));
    result.put("/aips/\\d+", Arrays.asList(APPLICATION, "xdbLibrary"));
    result.put("/applicationCategories/\\d+", Arrays.asList(TENANT));
    result.put("/applications/\\d+", Arrays.asList(TENANT));
    result.put("/audits/\\d+", Arrays.asList(APPLICATION, TENANT));
    result.put("/buckets/\\d+", Arrays.asList("spaceRootObject"));
    result.put("/confirmations/\\d+", Arrays.asList(APPLICATION, "deliveryChannel"));
    result.put("/customPresentationConfigurations/\\d+", Arrays.asList(APPLICATION, "exportPipeline", TENANT));
    result.put("/databases/\\d+", Arrays.asList(APPLICATION, CI_STORE, MANAGED_ITEM_STORE, XDB_STORE));
    result.put("/databaseCryptoes/\\d+", Arrays.asList(APPLICATION, "database"));
    result.put("/deliveryChannels/\\d+", Arrays.asList(APPLICATION, STORE));
    result.put("/exportConfigurations/\\d+", Arrays.asList("pipeline", TENANT, "transformation"));
    result.put("/exportPipelines/\\d+", Arrays.asList(APPLICATION, TENANT));
    result.put("/exportTransformations/\\d+", Arrays.asList(APPLICATION, TENANT));
    result.put("/holds/\\d+", Arrays.asList(TENANT));
    result.put("/holdings/\\d+", Arrays.asList(APPLICATION, CI_STORE, "ingest", "logStore", MANAGED_ITEM_STORE,
        "renditionStore", "sipStore", "stagingStore", "xdbLibraryPolicy", XDB_STORE, "xmlStore"));
    result.put("/holdingCryptoes/\\d+", Arrays.asList(APPLICATION, "holding"));
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
    result.put("/retentionPolicies/\\d+", Arrays.asList(TENANT));
    result.put("/schemas/\\d+", Arrays.asList("database", XDB_STORE));
    result.put("/searches/\\d+", Arrays.asList("aic", APPLICATION, "searchGroup", "query"));
    result.put("/searchGroups/\\d+", Arrays.asList(APPLICATION));
    result.put("/searchCompositions/\\d+", Arrays.asList("resultMaster", SEARCH));
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
    result.put("/xdbLibraries/\\d+", Arrays.asList(APPLICATION));
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
    result.put(CI_STORE, STORE);
    result.put(MANAGED_ITEM_STORE, STORE);
    result.put("xdbStore", STORE);
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
