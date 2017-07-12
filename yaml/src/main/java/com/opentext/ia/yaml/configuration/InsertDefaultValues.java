/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.opentext.ia.yaml.core.PathVisitor;
import com.opentext.ia.yaml.core.Visit;
import com.opentext.ia.yaml.core.YamlMap;
import com.opentext.ia.yaml.resource.ResourceResolver;


class InsertDefaultValues extends PathVisitor {

  private static final String FORMAT = "format";
  private static final String TYPE = "type";
  private static final String STRING = "STRING";
  private static final Map<String, Collection<Default>> DEFAULT_PROPERTIES_BY_PATH_REGEX = defaultValuesByPathRegex();

  private static Map<String, Collection<Default>> defaultValuesByPathRegex() {
    Map<String, Collection<Default>> result = new HashMap<>();
    result.put("/.+/path.value.index", Default.of("build.without.logging", false,
        "compressed", false,
        "concurrent", false,
        "unique.keys", true));
    result.put("/.+/full.text.index", Default.of("convert.terms.to.lowercase", true,
        "filter.english.stop.words", false,
        "include.attributes", false,
        "index.all.text", true,
        "optimize.leading.wildcard.search", true,
        "support.phrases", false,
        "support.scoring", false,
        "support.start.end.token.flags", false));
    result.put("/aics/\\d+/criteria/\\d+", Default.of("indexed", true,
        TYPE, STRING));
    result.put("/exportPipelines/\\d+", Default.of("collectionBased", false,
        "composite", true,
        "envelopeFormat", "zip",
        "includesContent", true,
        "inputFormat", "ROW_COLUMN"));
    result.put("/exportConfigurations/\\d+", Default.of("exportType", "ASYNCHRONOUS"));
    result.put("/fileSystemRoots/\\d+", Default.of(TYPE, "FILESYSTEM"));
    result.put("/holdings/\\d+", Default.of("ciHashValidationEnabled", true,
        "keepSipAfterCommitEnabled", false,
        "logStoreEnabled", true,
        "pdiXmlHashEnforced", false,
        "pdiXmlHashValidationEnabled", true,
        "syncCommitEnabled", true,
        "xdbMode", "PRIVATE"));
    result.put("/ingests/\\d+", Default.of("content", new YamlMap()
        .put(FORMAT, "xml")
        .put("text", ResourceResolver.fromClasspath().apply("defaultIngest.xml"))));
    result.put("/ingestNodes/\\d+", Default.of("enumerationCutoffDays", 30,
       "enumerationMaxResultCount", 10,
       "enumerationMinusRunning", true,
       "logLevel", "INFO"));
    result.put("/queries/\\d+", Default.of("resultRootElement", "result",
        "resultRootNsEnabled", true));
    result.put("/queries/\\d+/xdbPdiConfigs/operands/\\d+", Default.of(TYPE, STRING));
    result.put("/queryQuota", Default.of("aipQuota", 0,
        "aiuQuota", 0,
        "dipQuota", 0));
    result.put("/receiverNodes/\\d+", Default.of("logLevel", "INFO",
        "sips", Arrays.asList(
            new YamlMap()
                .put(FORMAT,  "sip_zip")
                .put("extractorImpl", "com.emc.ia.reception.sip.extractor.impl.ZipSipExtractor"),
            new YamlMap()
                .put(FORMAT, "eas_sip_zip")
                .put("extractorImpl", "com.emc.ia.reception.sip.extractor.impl.LegacyZipSipExtractor"))));
    result.put("/resultMasters/\\d+/panels/\\d+/tabs/\\d+/columns/\\d+", Default.of("sort", "NONE",
        TYPE, STRING));
    result.put("/searches/\\d+", Default.of("nested", false,
        "inUse", true));
    result.put("/stores/\\d+", Default.of("status", "ONLINE",
        "storeType", "REGULAR",
        TYPE, "FILESYSTEM"));
    return result;
  }

  InsertDefaultValues() {
    super(DEFAULT_PROPERTIES_BY_PATH_REGEX.keySet());
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
      Collection<Default> result = new ArrayList<>();
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
