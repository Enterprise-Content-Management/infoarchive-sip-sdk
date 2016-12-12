/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.rest.RestClient;

public final class FederationIaHandler extends BaseIAArtifact {

  public static FederationExtractor extractor() {
    return new FederationExtractor();
  }

  private final String name;
  private final String bootstrap;
  private final String superUserPassword;

  public FederationIaHandler() {
    this("mainFederation", null, null);
  }

  public FederationIaHandler(String name, String bootstrap, String superUserPassword) {
    this.name = Objects.requireNonNull(name);
    this.bootstrap = Optional.ofNullable(bootstrap).orElse("xhive://127.0.0.1:2910");
    this.superUserPassword = Optional.ofNullable(superUserPassword).orElse("test");
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Federations federations = client.follow(cache.getFirst(Services.class), LINK_FEDERATIONS, Federations.class);
    Federation createdFederation = federations.byName(name);
    if (createdFederation == null) {
      createdFederation = client.createCollectionItem(federations, buildFederation(), LINK_ADD, LINK_SELF);
    } else {
      ensureFederation(createdFederation);
    }
    cache.cacheOne(createdFederation);
  }

  private Federation buildFederation() {
    Federation federation = new Federation();
    federation.setName(name);
    federation.setBootstrap(bootstrap);
    federation.setSuperUserPassword(superUserPassword);
    return federation;
  }

  private void ensureFederation(Federation currentFederation) throws IOException {
    if (!bootstrap.equals(currentFederation.getBootstrap())
            || !superUserPassword.equals(currentFederation.getSuperUserPassword())) {
      throw new IllegalStateException("Current federation is different from configured.");
    }
  }

  public boolean equals(Object other) {
    if (!(other instanceof FederationIaHandler)) {
      return false;
    }
    FederationIaHandler handler = (FederationIaHandler)other;
    return Objects.equals(name, handler.name)
        && Objects.equals(bootstrap, handler.bootstrap)
        && Objects.equals(superUserPassword, handler.superUserPassword);
  }

  public int hashCode() {
    return Objects.hash(name, bootstrap, superUserPassword);
  }

  private static final class FederationExtractor extends ArtifactExtractor {
    @Override
    @SuppressWarnings("rawtypes")
    public FederationIaHandler extract(Object representation) {
      Map federationRepresentation = asMap(representation);
      String name = extractName(federationRepresentation);
      String bootstrap = extractString(federationRepresentation, "bootstrap");
      String superUserPassword = extractString(federationRepresentation, "superUserPassword");
      return new FederationIaHandler(name, bootstrap, superUserPassword);
    }

    @Override
    public String getFieldName() {
      return "federation";
    }
  }
}
