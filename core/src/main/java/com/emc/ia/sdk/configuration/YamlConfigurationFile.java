package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;
import com.emc.ia.sdk.sip.client.dto.Tenant;

/**
 * Abstracts from the actual representation of the file as well as from parser implementation
 */

public interface YamlConfigurationFile {
  <T extends NamedLinkContainer> T getNamedObject(Class<T> typeToken); // This is mostly the class for future use
  Tenant getTenant();
  Federation getFederation();
  Application getApplication();
}
