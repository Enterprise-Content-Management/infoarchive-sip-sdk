package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;

import java.util.Map;

@FunctionalInterface
public interface Extractor<T extends NamedLinkContainer> {
  T extract(String key, Map source);
}
