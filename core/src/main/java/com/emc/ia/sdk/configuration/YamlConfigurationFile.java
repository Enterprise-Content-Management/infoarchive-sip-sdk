package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Tenant;

/**
 * Abstracts from the actual representation of the file as well as from parser implementation
 */

public interface YamlConfigurationFile {
  Tenant getTenant();
  Application getApplication();
  // TODO: Possibly too many methods. Figure out how to conveniently split it.
}
