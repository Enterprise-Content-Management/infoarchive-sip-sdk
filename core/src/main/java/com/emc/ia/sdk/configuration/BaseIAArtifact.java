package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public abstract class BaseIAArtifact implements Installable, InfoArchiveLinkRelations {

  @Override
  public final void install(RestClient client, IACache cache) {
    try {
      installArtifact(client, cache);
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
  }

  public abstract void installArtifact(RestClient client, IACache cache) throws IOException;
}
