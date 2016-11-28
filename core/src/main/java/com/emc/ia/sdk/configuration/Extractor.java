package com.emc.ia.sdk.configuration;

/**
 * Class that is able to extract object from the Map/List/String representation that is fetched with SnakeYaml
 */
public interface Extractor {
  Installable extract(Object representation);
  String getFieldName();
}
