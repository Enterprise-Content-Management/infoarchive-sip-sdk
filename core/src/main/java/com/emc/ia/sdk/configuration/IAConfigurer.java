package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.configurer.InfoArchiveConfigurer;
import com.emc.ia.sdk.sip.client.ArchiveClient;

public interface IAConfigurer extends InfoArchiveConfigurer {
  ArchiveClient createArchiveClient();
//  IARemoteSnapshot snapshotConfiguration();
}
