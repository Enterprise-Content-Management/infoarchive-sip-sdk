package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;

/**
 * Class that is able to extract object from the Map/List/String representation that is fetched with SnakeYaml
 * @param <T> Class of extracted object
 */
@FunctionalInterface
public interface Extractor<T extends NamedLinkContainer> {
  T extract(Object representation);
}
