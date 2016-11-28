package com.emc.ia.sdk.configuration;


import com.emc.ia.sdk.support.rest.RestClient;

import java.util.List;

/**
 * Implements Installable artifact, represents group of artifacts with the same alias type.
 * Delegates installation to the each contained.
 */

public final class ArtifactGroup implements Installable {

  private final List<BaseIAArtifact> artifacts;

  public ArtifactGroup(List<BaseIAArtifact> artifacts) {
    this.artifacts = artifacts;
  }

  @Override
  public void install(RestClient client, IACache cache) {
    artifacts.forEach(artifact -> artifact.install(client, cache));
  }
}
