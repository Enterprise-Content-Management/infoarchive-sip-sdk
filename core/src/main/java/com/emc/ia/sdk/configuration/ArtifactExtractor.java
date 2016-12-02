package com.emc.ia.sdk.configuration;


import java.util.Map;
import java.util.Objects;

/**
 * Implementation of Extractor that represents single InfoArchive artifact object.
 * Also provides utility methods for future use during extraction.
 */

public abstract class ArtifactExtractor implements Extractor {

  public abstract BaseIAArtifact extract(Object representation);

  protected final String extractString(Map configuration, String key) {
    return (String) configuration.get(key);
  }

  protected final String extractName(Map configuration) {
    return extractString(configuration, "name");
  }

  protected final Map asMap(Object representation) {
    return (Map) representation;
  }

  protected final String asString(Object representation) {
    return (String) representation;
  }

}
