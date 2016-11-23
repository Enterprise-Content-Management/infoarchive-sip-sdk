package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public abstract class BaseIAArtifact implements InfoArchiveLinkRelations {
  public abstract void install(RestClient client, IACache cache) throws IOException;
}
