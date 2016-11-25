package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.io.RuntimeIoException;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public final class IAYamlConfigurer implements IAConfigurer {

  private final RestClient client;
  private final ArtifactCollection artifacts;

  private final IACache cache = new IACache();

  public IAYamlConfigurer(RestClient client, String servicesUri, ArtifactCollection artifacts) throws IOException {
    this.client = client;
    this.artifacts = artifacts;
    cache.cacheOne(client.get(servicesUri, Services.class));
  }

  @Override
  public void configure() {
    artifacts.forEach(this::installArtifact);
  }

  @Override
  public ArchiveClient createArchiveClient() {
    throw new UnsupportedOperationException();
  }

  private void installArtifact(BaseIAArtifact artifact) {
    try {
      artifact.install(client, cache);
    } catch (IOException ex) {
      throw new RuntimeIoException(ex);
    }
  }
}
