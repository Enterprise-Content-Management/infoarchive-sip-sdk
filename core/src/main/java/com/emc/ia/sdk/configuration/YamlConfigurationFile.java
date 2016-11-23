package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.NamedLinkContainer;
import com.emc.ia.sdk.sip.client.dto.Tenant;

import java.util.List;

/**
 * Abstracts from the actual representation of the file as well as from parser implementation
 */

public interface YamlConfigurationFile {
  BaseIAArtifact extractWith(Extractor extractor);
}
