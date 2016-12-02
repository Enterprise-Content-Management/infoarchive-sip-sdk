package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.configurer.InfoArchiveConfigurer;
import com.emc.ia.sdk.sip.client.ArchiveClient;

/**
 * Extends InfoArchiveConfigurer by adding more methods to the interface that makes it more convenient to use.
 */
public interface IAConfigurer extends InfoArchiveConfigurer {
  ArchiveClient createArchiveClient();
}
