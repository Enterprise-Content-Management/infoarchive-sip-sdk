/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.server.configuration.yaml;

import java.util.*;

import com.opentext.ia.sdk.support.resource.ResourceResolver;
import com.opentext.ia.sdk.support.yaml.Visit;
import com.opentext.ia.sdk.support.yaml.YamlMap;


public class InsertDefaultValues extends PathVisitor {

  @SuppressWarnings({ "serial", "rawtypes", "unchecked" })
  private static final Map<String, Collection<Default>> DEFAULT_PROPERTIES_BY_PATH_REGEX = new HashMap() {{
    put("/appExportPipelines/\\d", Default.of("envelopeFormat", "gzip",
        "includesContent", true,
        "inputFormat", "ROW_COLUMN"));
    put("/appExportConfigurations/\\d", Default.of("exportType", "asynchronous"));
    put("/fullTextIndexes/\\d", Default.of("convert.terms.to.lowercase", true,
        "filter.english.stop.words", false,
        "include.attributes", false,
        "index.all.text", true,
        "optimize.leading.wildcard.search", true,
        "support.phrases", false,
        "support.scoring", false,
        "support.start.end.token.flags", false));
    put("/holdings/\\d", Default.of("ciHashValidationEnabled", true,
        "keepSipAfterCommitEnabled", false,
        "logStoreEnabled", true,
        "pdiXmlHashEnforced", false,
        "pdiXmlHashValidationEnabled", true,
        "syncCommitEnabled", true,
        "xdbMode", "PRIVATE"));
    put("/ingests/\\d", Default.of("content", new YamlMap()
        .put("format", "xml")
        .put("text", ResourceResolver.fromClasspath().apply("defaultIngest.xml"))));
    put("/ingestNodes/\\d", Default.of("enumerationCutoffDays", 30,
       "enumerationMaxResultCount", 10,
       "enumerationMinusRunning", true,
       "logLevel", "INFO"));
    put("/.*/pathValueIndex", Default.of("buildWithoutLogging", false,
        "compressed", false,
        "concurrent", false,
        "uniqueKeys", true));
    put("/queries/\\d", Default.of("resultRootElement", "result"));
    put("/queryQuota", Default.of("aipQuota", 0,
        "aiuQuota", 0,
        "dipQuota", 0));
    put("/receiverNodes/\\d", Default.of("logLevel", "INFO",
        "sips", Arrays.asList(
            new YamlMap()
                .put("format",  "sip_zip")
                .put("extractorImpl", "com.emc.ia.reception.sip.extractor.impl.ZipSipExtractor"),
            new YamlMap()
                .put("format", "eas_sip_zip")
                .put("extractorImpl", "com.emc.ia.reception.sip.extractor.impl.LegacyZipSipExtractor"))));
    put("/stores/\\d", Default.of("status", "ONLINE",
        "storeType", "REGULAR",
        "type", "FILESYSTEM"));
  }};

  public InsertDefaultValues() {
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
