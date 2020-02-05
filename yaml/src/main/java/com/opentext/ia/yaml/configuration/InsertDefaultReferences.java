/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
  private static final String DATABASE = "database";
  private static final String AIC = "aic";
  private static final String QUERY = "query";
  private static final String STORE = "store";
  private static final String CI_STORE = "ciStore";
  private static final String LOG_STORE = "logStore";
  private static final String MANAGED_ITEM_STORE = "managedItemStore";
  private static final String RENDITION_STORE = "renditionStore";
  private static final String SIP_STORE = "sipStore";
  private static final String STAGING_STORE = "stagingStore";
  private static final String XDB_STORE = "xdbStore";
  private static final String XML_STORE = "xmlStore";
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
    put(result, "/.*/content", APPLICATION, STORE);
    put(result, "/.+/((.+\\.)?q|.+Q)uery", NAMESPACE);
    put(result, "/accessNodes/\\d+", APPLICATION);
    put(result, "/aics/\\d+", APPLICATION);
    put(result, "/aips/\\d+", APPLICATION, "xdbLibrary");
    put(result, "/applicationCategories/\\d+", TENANT);
    put(result, "/applications/\\d+", TENANT);
    put(result, "/buckets/\\d+", "spaceRootObject");
    put(result, "/confirmations/\\d+", APPLICATION, "deliveryChannel");
    put(result, "/customPresentationConfigurations/\\d+", EXPORT_PIPELINE);
    put(result, "/databases/\\d+", APPLICATION, CI_STORE, MANAGED_ITEM_STORE, XDB_STORE);
    put(result, "/databaseCryptoes/\\d+", APPLICATION, DATABASE);
    put(result, "/deliveryChannels/\\d+", APPLICATION, STORE);
    put(result, "/exportConfigurations/\\d+", "pipeline", "transformation");
    put(result, "/holds/\\d+", TENANT);
    put(result, "/holdings/\\d+", APPLICATION, CI_STORE, "ingest", LOG_STORE, MANAGED_ITEM_STORE,
        RENDITION_STORE, SIP_STORE, STAGING_STORE, "xdbLibraryPolicy", XDB_STORE, XML_STORE);
    put(result, "/holdings/\\d+/ingestConfigs/\\d+", "ingest");
    put(result, "/holdings/\\d+/pdiConfigs/\\d+", "pdi");
    put(result, "/holdingCryptoes/\\d+", APPLICATION, "holding");
    put(result, "/holdingCryptoes/\\d+/pdis/\\d+", "pdiCrypto");
    put(result, "/ingests/\\d+", APPLICATION);
    put(result, "/ingestNodes/\\d+", APPLICATION);
    put(result, "/orders/\\d+", APPLICATION);
    put(result, "/pdis/\\d+", APPLICATION);
    put(result, "/pdis/\\d+/context/text/data/\\d+/indexes", NAMESPACE);
    put(result, "/pdiCryptoes/\\d+", APPLICATION);
    put(result, "/pdiSchemas/\\d+", APPLICATION, NAMESPACE);
    put(result, "/queries/\\d+", APPLICATION, NAMESPACE, "order");
    put(result, "/queries/[^/]+/xdbPdiConfigs", NAMESPACE);
    put(result, "/queryQuotas/\\d+", APPLICATION);
    put(result, "/receiverNodes/\\d+", APPLICATION);
    put(result, "/resultConfigurationHelpers/\\d+", APPLICATION, NAMESPACE);
    put(result, "/resultMasters/\\d+", SEARCH, SEARCH_COMPOSITION);
    put(result, "/resultMasters/\\d+/panels/\\d+/tabs/\\d+/customPresentation", APPLICATION, TENANT);
    put(result, "/retentionPolicies/\\d+", TENANT);
    put(result, "/rules/\\d+", APPLICATION);
    put(result, "/schemas/\\d+", DATABASE, XDB_STORE);
    put(result, "/searches/\\d+", AIC, APPLICATION, DATABASE, QUERY);
    put(result, "/searchDebugs/\\d+", SEARCH);
    put(result, "/searchGroups/\\d+", APPLICATION);
    put(result, "/searchCompositions/\\d+", SEARCH);
    put(result, "/spaces/\\d+", APPLICATION);
    put(result, "/spaceRootFolders/\\d+", "fileSystemRoot", SPACE);
    put(result, "/spaceRootObjects/\\d+", SPACE, "customStorage", "storageEndPointCredential");
    put(result, "/spaceRootXdbLibraries/\\d+", SPACE, "xdbDatabase");
    put(result, "/stores/\\d+", APPLICATION, "bucket", "fileSystemFolder");
    put(result, "/storageEndPointCredentials/\\d+", "storageEndPoint");
    put(result, "/tables/\\d+", APPLICATION, "schema");
    put(result, "/transformations/\\d+", APPLICATION);
    put(result, "/valueLists/\\d+", APPLICATION);
    put(result, "/xdbLibraries/\\d+", APPLICATION);
    put(result, "/xdbLibraryPolicies/\\d+", APPLICATION);
    put(result, "/xforms/\\d+", SEARCH, SEARCH_COMPOSITION);
    put(result, "/xqueries/\\d+", SEARCH, SEARCH_COMPOSITION);
    return result;
  }

  private static void put(Map<String, Collection<String>> map, String key, String item) {
    map.put(key, Collections.singletonList(item));
  }

  private static void put(Map<String, Collection<String>> map, String key, String... items) {
    Collection<String> collection = Arrays.asList(items);
    map.put(key, collection);
  }

  private static Map<String, String> typeByReferenceProperty() {
    Map<String, String> result = new HashMap<>();
    result.put("pipeline", EXPORT_PIPELINE);
    result.put("transformation", "exportTransformation");
    result.put(CI_STORE, STORE);
    result.put(LOG_STORE, STORE);
    result.put(MANAGED_ITEM_STORE, STORE);
    result.put(RENDITION_STORE, STORE);
    result.put(SIP_STORE, STORE);
    result.put(STAGING_STORE, STORE);
    result.put(XDB_STORE, STORE);
    result.put(XML_STORE, STORE);
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
  @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CollapsibleIfStatements"})
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
    if (isSearchesPath(visit)) {
        if ((AIC.equals(property) || QUERY.equals(property)) && yaml.containsKey(DATABASE)
            || DATABASE.equals(property) && (yaml.containsKey(AIC) || yaml.containsKey(QUERY))) {
          return false;
      }
    }
    return true;
  }

  private boolean isSearchesPath(Visit visit) {
    return visit.getPath().startsWith("/searches/");
  }

  @Override
  protected String typeOf(String property) {
    return TYPE_BY_REFERENCE_PROPERTY.getOrDefault(property, property);
  }

}
