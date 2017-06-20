/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;

import java.util.*;

import com.opentext.ia.sdk.yaml.core.PathVisitor;
import com.opentext.ia.sdk.yaml.core.Visit;
import com.opentext.ia.sdk.yaml.core.YamlMap;
import com.opentext.ia.sdk.yaml.resource.ResourceResolver;


class InsertDefaultValues extends PathVisitor {

  private static final String FORMAT = "format";
  private static final Map<String, Collection<Default>> DEFAULT_PROPERTIES_BY_PATH_REGEX = defaultValuesByPathRegex();

  private static Map<String, Collection<Default>> defaultValuesByPathRegex() {
    Map<String, Collection<Default>> result = new HashMap<>();
    result.put("/.+/path.value.index", Default.of("buildWithoutLogging", false,
        "compressed", false,
        "concurrent", false,
        "uniqueKeys", true));
    result.put("/.+/full.text.index", Default.of("convert.terms.to.lowercase", true,
        "filter.english.stop.words", false,
        "include.attributes", false,
        "index.all.text", true,
        "optimize.leading.wildcard.search", true,
        "support.phrases", false,
        "support.scoring", false,
        "support.start.end.token.flags", false));
    result.put("/appExportPipelines/\\d", Default.of("envelopeFormat", "gzip",
        "includesContent", true,
        "inputFormat", "ROW_COLUMN"));
    result.put("/appExportConfigurations/\\d", Default.of("exportType", "asynchronous"));
    result.put("/fullTextIndexes/\\d", Default.of("convert.terms.to.lowercase", true,
        "filter.english.stop.words", false,
        "include.attributes", false,
        "index.all.text", true,
        "optimize.leading.wildcard.search", true,
        "support.phrases", false,
        "support.scoring", false,
        "support.start.end.token.flags", false));
    result.put("/holdings/\\d", Default.of("ciHashValidationEnabled", true,
        "keepSipAfterCommitEnabled", false,
        "logStoreEnabled", true,
        "pdiXmlHashEnforced", false,
        "pdiXmlHashValidationEnabled", true,
        "syncCommitEnabled", true,
        "xdbMode", "PRIVATE"));
    result.put("/ingests/\\d", Default.of("content", new YamlMap()
        .put(FORMAT, "xml")
        .put("text", ResourceResolver.fromClasspath().apply("defaultIngest.xml"))));
    result.put("/ingestNodes/\\d", Default.of("enumerationCutoffDays", 30,
       "enumerationMaxResultCount", 10,
       "enumerationMinusRunning", true,
       "logLevel", "INFO"));
    result.put("/.*/pathValueIndex", Default.of("buildWithoutLogging", false,
        "compressed", false,
        "concurrent", false,
        "uniqueKeys", true));
    result.put("/queries/\\d", Default.of("resultRootElement", "result",
        "resultRootNsEnabled", true));
    result.put("/queryQuota", Default.of("aipQuota", 0,
        "aiuQuota", 0,
        "dipQuota", 0));
    result.put("/receiverNodes/\\d", Default.of("logLevel", "INFO",
        "sips", Arrays.asList(
            new YamlMap()
                .put(FORMAT,  "sip_zip")
                .put("extractorImpl", "com.emc.ia.reception.sip.extractor.impl.ZipSipExtractor"),
            new YamlMap()
                .put(FORMAT, "eas_sip_zip")
                .put("extractorImpl", "com.emc.ia.reception.sip.extractor.impl.LegacyZipSipExtractor"))));
    result.put("/stores/\\d", Default.of("status", "ONLINE",
        "storeType", "REGULAR",
        "type", "FILESYSTEM"));
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
