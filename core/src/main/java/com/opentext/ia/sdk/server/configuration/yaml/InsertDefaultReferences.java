/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.*;

import org.atteo.evo.inflector.English;

import com.opentext.ia.sdk.support.yaml.Value;
import com.opentext.ia.sdk.support.yaml.Visit;


public class InsertDefaultReferences extends InsertDefaultVisitor {

  private static final String TENANT = "tenant";
  private static final String APPLICATION = "application";
  private static final String SEARCH = "search";
  private static final String NAME = "name";
  private static final String DEFAULT = "default";
  @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
  private static final Map<String, Collection<String>> REFERENCE_PROPERTIES_BY_PATH_REGEX = new HashMap() {{
    put("/.*/content", Arrays.asList(APPLICATION, "store"));
    put("/aics/\\d", Arrays.asList(APPLICATION));
    put("/aips/\\d", Arrays.asList(APPLICATION, "xdbLibrary"));
    put("/applicationCategories/\\d", Arrays.asList(TENANT));
    put("/applications/\\d", Arrays.asList(TENANT));
    put("/buckets/\\d", Arrays.asList("spaceRootObject"));
    put("/confirmations/\\d", Arrays.asList(APPLICATION, "deliveryChannel"));
    put("/customPresentationConfigurations/\\d", Arrays.asList("exportPipeline", TENANT));
    put("/databases/\\d", Arrays.asList(APPLICATION, "ciStore", "managedItemStore", "xdbStore"));
    put("/databaseCryptoes/\\d", Arrays.asList(APPLICATION, "database"));
    put("/deliveryChannels/\\d", Arrays.asList(APPLICATION, "store"));
    put("/exportConfigurations/\\d", Arrays.asList("exportPipeline", TENANT));
    put("/exportPipelines/\\d", Arrays.asList(APPLICATION, TENANT));
    put("/exportTransformations/\\d", Arrays.asList(APPLICATION, TENANT));
    put("/fileSystemFolders/\\d", Arrays.asList("spaceRootFolder"));
    put("/holdings/\\d", Arrays.asList(APPLICATION, "ciStore", "ingest", "logStore", "managedItemStore", "pdi",
        "renditionStore", "sipStore", "stagingStore", "xdbLibrary", "xdbLibraryPolicy", "xdbStore", "xmlStore"));
    put("/holdingCryptoes/\\d", Arrays.asList(APPLICATION, "cryptoObject", "holding", "pdiCrypto"));
    put("/ingests/\\d", Arrays.asList(APPLICATION));
    put("/ingestNodes/\\d", Arrays.asList(APPLICATION));
    put("/orders/\\d", Arrays.asList(APPLICATION));
    put("/pdis/\\d", Arrays.asList(APPLICATION));
    put("/pdiCryptoes/\\d", Arrays.asList(APPLICATION));
    put("/pdiSchemas/\\d", Arrays.asList(APPLICATION));
    put("/queries/\\d", Arrays.asList(APPLICATION, "order", "queryQuota"));
    put("/queryQuotas/\\d", Arrays.asList(APPLICATION));
    put("/receiverNodes/\\d", Arrays.asList(APPLICATION));
    put("/resultConfigurationHelpers/\\d", Arrays.asList(APPLICATION));
    put("/resultMasters/\\d", Arrays.asList(SEARCH, "searchComposition"));
    put("/schemas/\\d", Arrays.asList("database", "xdbStore"));
    put("/searches/\\d", Arrays.asList(APPLICATION, "searchGroup"));
    put("/searchGroups/\\d", Arrays.asList(APPLICATION));
    put("/searchCompositions/\\d", Arrays.asList(SEARCH));
    put("/spaces/\\d", Arrays.asList(APPLICATION));
    put("/spaceRootFolders/\\d", Arrays.asList("fileSystemRoot", "space"));
    put("/spaceRootObjects/\\d", Arrays.asList("space"));
    put("/spaceRootXdbLibraries/\\d", Arrays.asList("space", "xdbDatabase"));
    put("/stores/\\d", Arrays.asList(APPLICATION));
    put("/storageEndPointCredentials/\\d", Arrays.asList("storageEndPoint"));
    put("/tables/\\d", Arrays.asList(APPLICATION, "schema"));
    put("/tranformations/\\d", Arrays.asList(APPLICATION));
    put("/valueLists/\\d", Arrays.asList(APPLICATION));
    put("/xdbDatabases/\\d", Arrays.asList("xdbFederation"));
    put("/xdbLibraries/\\d", Arrays.asList(APPLICATION, "spaceRootXdbLibrary"));
    put("/xdbLibraryPolicies/\\d", Arrays.asList(APPLICATION));
    put("/xforms/\\d", Arrays.asList(SEARCH, "searchComposition"));
    put("/xqueries/\\d", Arrays.asList(SEARCH, "searchComposition"));
    put("/xqueryModules/\\d", Arrays.asList(APPLICATION, TENANT));
  }};

  public InsertDefaultReferences() {
    super(REFERENCE_PROPERTIES_BY_PATH_REGEX);
  }

  @Override
  protected String getDefaultValueFor(Visit visit, String type) {
    List<Value> instances = visit.getRootMap().get(English.plural(type)).toList();
    if (instances.size() == 1) {
      return instances.get(0).toMap().get(NAME).toString();
    }
    return instances.stream()
        .map(Value::toMap)
        .filter(map -> map.get(DEFAULT).toBoolean())
        .map(map -> map.get(NAME).toString())
        .findAny()
        .orElse(null);
  }

}
