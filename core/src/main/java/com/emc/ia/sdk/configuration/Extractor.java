package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;

/**
 * Class that is able to extract object from the Map/List/String representation that is fetched with SnakeYaml
 */
public interface Extractor {
  BaseIAArtifact extract(Object representation);
  String getFieldName();
}
