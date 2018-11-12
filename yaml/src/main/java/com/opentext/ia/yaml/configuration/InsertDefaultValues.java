/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


class InsertDefaultValues extends PathVisitor {

  private static final String ENCODING = "encoding";
  private static final String CONFIGURE = "configure";
  private static final String TYPE = "type";
  private static final String STRING = "STRING";
  private static final String NONE = "NONE";
  private static final String STATIC = "STATIC";
  private static final String BASE64 = "base64";
  private static final Map<String, Collection<Default>> DEFAULT_PROPERTIES_BY_PATH_REGEX = defaultValuesByPathRegex();

  @SuppressWarnings("PMD.ExcessiveMethodLength")
  private static Map<String, Collection<Default>> defaultValuesByPathRegex() {
    Map<String, Collection<Default>> result = new HashMap<>(32 * 4 / 3);
    result.put("/.+/path.value.index", Default.of("build.without.logging", Boolean.FALSE,
        "compressed", Boolean.FALSE,
        "concurrent", Boolean.FALSE,
        "unique.keys", Boolean.TRUE));
    result.put("/.+/full.text.index", Default.of("convert.terms.to.lowercase", Boolean.TRUE,
        "filter.english.stop.words", Boolean.FALSE,
        "include.attributes", Boolean.FALSE,
        "index.all.text", Boolean.TRUE,
        "optimize.leading.wildcard.search", Boolean.TRUE,
        "support.phrases", Boolean.FALSE,
        "support.scoring", Boolean.FALSE,
        "support.start.end.token.flags", Boolean.FALSE));

    result.put("/aics/\\d+/criteria/\\d+", Default.of("indexed", Boolean.FALSE,
        TYPE, STRING));
    result.put("/applications/\\d+", Default.of("cryptoEncoding", BASE64,
        "metadataCacheSize", 0,
        "retentionEnabled", Boolean.FALSE,
        "searchCreated", Boolean.TRUE,
        "state", "IN_TEST",
        "structuredDataStorageAllocationStrategy", "DEFAULT"));
    result.put("/auditEvents/\\d+", Default.of("enabled", Boolean.TRUE));
    result.put("/cryptoObjects/\\d+", Default.of("encryptionAlgorithm", "AES",
        "encryptionMode", "CBC",
        "keySize", 256));
    result.put("/deliveryChannels/\\d+", Default.of("compress", Boolean.FALSE,
        "overwrite", Boolean.FALSE));
    result.put("/exportPipelines/\\d+", Default.of("collectionBasedExport", Boolean.FALSE,
        "composite", Boolean.TRUE,
        "envelopeFormat", "zip",
        "includesContent", Boolean.TRUE,
        "inputFormat", "ROW_COLUMN",
        TYPE, "XPROC"));
    result.put("/exportConfigurations/\\d+", Default.of("cryptoEncoding", BASE64,
        "exportType", "ASYNCHRONOUS"
    ));
    result.put("/fileSystemRoots/\\d+", Default.of(TYPE, "FILESYSTEM"));
    result.put("/holdingCryptoes/\\d+/(ci|pdi|sip)", Default.of("cryptoEnabled", Boolean.FALSE));
    result.put("/holdings/\\d+", Default.of("ciHashValidationEnabled", Boolean.TRUE,
        "keepCiOnRejInvEnabled", Boolean.FALSE,
        "keepPdiXmlAfterIngestEnabled", Boolean.TRUE,
        "keepSipOnRejInvEnabled", Boolean.FALSE,
        "keepXmlOnRejInvEnabled", Boolean.FALSE,
        "keepSipAfterCommitEnabled", Boolean.FALSE,
        "logStoreEnabled", Boolean.TRUE,
        "managedItemPartitionScheme", "DAILY",
        "pdiXmlHashEnforced", Boolean.FALSE,
        "pdiXmlHashValidationEnabled", Boolean.TRUE,
        "pushRetentionOnRejInvEnabled", Boolean.FALSE,
        "syncCommitEnabled", Boolean.TRUE,
        "xdbMode", "PRIVATE"));
    result.put("/ingestNodes/\\d+", Default.of("enumerationMaxResultCount", 10,
       "enumerationMinusRunning", Boolean.FALSE,
       "logLevel", "INFO"));
    result.put("/queries/\\d+", Default.of("resultRootElement", "result",
        "resultRootNsEnabled", Boolean.TRUE));
    result.put("/queries/\\d+/xdbPdiConfigs/\\d+", Default.of("template", "return $aiu"));
    result.put("/queries/\\d+/xdbPdiConfigs/\\d+/operands/\\d+", Default.of(TYPE, STRING,
        "index", Boolean.FALSE));
    result.put("/queryQuotas/\\d+", Default.of("aipQuota", 0,
        "aiuQuota", 0,
        "dipQuota", 0));
    result.put("/receiverNodes/\\d+", Default.of("logLevel", "INFO"));
    result.put("/resultConfigurationHelpers/\\d+", Default.of("propagateChanges", Boolean.FALSE));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+", Default.of("createCollectionEnabled", Boolean.FALSE,
        "exportEnabled", Boolean.FALSE));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+/columns/\\d+", Default.of(
        "dataType", STRING,
        "defaultSort", NONE,
        "downloadable", Boolean.TRUE,
        "encrypt", Boolean.FALSE,
        "exportable", Boolean.TRUE,
        "filterEnabled", Boolean.FALSE,
        "hidden", Boolean.FALSE,
        "masked", Boolean.FALSE,
        "order", 0,
        "previewRequired", Boolean.TRUE,
        "printable", Boolean.TRUE,
        "rowIdentifier", Boolean.FALSE,
        "showIcon", Boolean.FALSE,
        "sortable", Boolean.FALSE));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+/columns/\\d+/urlConfiguration", Default.of(
        "hostType", STATIC,
        "pathType", NONE,
        "port", "80",
        "portType", NONE,
        "protocol", "http",
        "protocolType", STATIC,
        "queryType", NONE,
        "target", "TAB"));
    result.put("/retentionPolicies/\\d+", Default.of("dispositionBlocked", Boolean.FALSE));
    result.put("/searches/\\d+", Default.of("nestedSearch", Boolean.FALSE,
        "state", "DRAFT"));
    result.put("/stores/\\d+", Default.of("status", "ONLINE",
        "contentLifetimeAfterRestoration", 0,
        "storeType", "REGULAR",
        TYPE, "FILESYSTEM"));
    result.put("/transformations/\\d+", Default.of("compressed", Boolean.TRUE,
        ENCODING, "UTF-8",
        TYPE, "XQUERY"));
    result.put("/xdbDatabases/\\d+", Default.of(ENCODING, BASE64));
    result.put("/xdbFederations/\\d+", Default.of(ENCODING, BASE64));
    result.put("/xdbLibraries/\\d+", Default.of("aipCount", 0,
        "aiuCount", 0,
        "cacheInCount", 0,
        "cacheSupport", Boolean.FALSE,
        "closeRequested", Boolean.FALSE,
        "closed", Boolean.FALSE,
        "concurrent", Boolean.FALSE,
        "detachable", Boolean.FALSE,
        "detached", Boolean.FALSE,
        "indexSize", 0,
        "readOnly", Boolean.FALSE,
        "size", 0,
        TYPE, "DATA",
        "xdbMode", "PRIVATE"
        ));
    result.put("/xdbLibraryPolicies/\\d+", Default.of("aiuThreshold", 0));
    result.put("/xforms/\\d+", Default.of("creator", "COMPOSITION"));
    return result;
  }

  InsertDefaultValues() {
    super(DEFAULT_PROPERTIES_BY_PATH_REGEX.keySet());
  }

  @Override
  public boolean test(Visit visit) {
    if (!super.test(visit)) {
      return false;
    }
    return ObjectConfiguration.parse(visit.getMap().get(CONFIGURE).toString()).mayCreateObject();
  }

  @Override
  public void accept(Visit visit) {
    pathRegexesMatching(visit)
        .flatMap(regex -> DEFAULT_PROPERTIES_BY_PATH_REGEX.get(regex).stream())
        .forEach(def -> def.insert(visit.getMap()));
  }


  private static class Default {

    private final String property;
    private final Object value;

    static Collection<Default> of(Object... propertiesAndValues) {
      Collection<Default> result = new ArrayList<>(propertiesAndValues.length / 2);
      int i = 0;
      while (i < propertiesAndValues.length) {
        String property = (String)propertiesAndValues[i++];
        Object value = propertiesAndValues[i++];
        result.add(new Default(property, value));
      }
      return result;
    }

    Default(String property, Object value) {
      this.property = property;
      this.value = value;
    }

    void insert(YamlMap map) {
      if (!map.containsKey(property)) {
        map.put(property, value);
      }
    }

  }

}
