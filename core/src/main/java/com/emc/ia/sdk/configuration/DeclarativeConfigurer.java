/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration;

import java.io.IOException;

import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.rest.RestClient;

public final class DeclarativeConfigurer implements IAConfigurer {

  private final RestClient client;
  private final ArtifactCollection artifacts;

  private final IACache cache = new IACache();

  public DeclarativeConfigurer(RestClient client, String servicesUri, ArtifactCollection artifacts) throws IOException {
    this.client = client;
    this.artifacts = artifacts;
    cache.cacheOne(client.get(servicesUri, Services.class));
  }

  @Override
  public void configure() {
    artifacts.forEach(installable -> installable.install(client, cache));
  }

  @Override
  public ArchiveClient createArchiveClient() {
    throw new UnsupportedOperationException();
  }
}