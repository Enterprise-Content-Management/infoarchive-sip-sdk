/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import java.util.Map;

/**
 * Implementation of Extractor that represents single InfoArchive artifact object.
 * Also provides utility methods for future use during extraction.
 */

public abstract class ArtifactExtractor implements Extractor {

  public abstract BaseIAArtifact extract(Object representation);

  protected final String extractString(Map<String, Object> configuration, String key) {
    return (String)configuration.get(key);
  }

  protected final String extractName(Map<String, Object> configuration) {
    return extractString(configuration, "name");
  }

  @SuppressWarnings("unchecked")
  protected final Map<String, Object> asMap(Object representation) {
    return (Map<String, Object>)representation;
  }

  protected final String asString(Object representation) {
    return (String)representation;
  }

}
