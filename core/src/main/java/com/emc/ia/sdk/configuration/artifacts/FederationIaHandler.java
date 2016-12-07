/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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

  private final Federation federation;

  public FederationIaHandler(Federation source) {
    this.federation = new Federation();
    federation.setName(source.getName());
    federation.setBootstrap(source.getBootstrap());
    federation.setSuperUserPassword(source.getSuperUserPassword());
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Federations federations = client.follow(cache.getFirst(Services.class), LINK_FEDERATIONS, Federations.class);
    Federation createdFederation = federations.byName(federation.getName());
    if (createdFederation == null) {
      createdFederation = createFederation(client, federations);
    } else {
      createdFederation = updateFederation(client, createdFederation);
    }
    cache.cacheOne(createdFederation);
  }

  private Federation createFederation(RestClient client, Federations container) throws IOException {
    fillDefaults();
    return client.createCollectionItem(container, federation, LINK_ADD, LINK_SELF);
  }

  private Federation updateFederation(RestClient client, Federation currentFederation) throws IOException {
    if (federation.getBootstrap() != null) {
      currentFederation.setBootstrap(federation.getBootstrap());
    }
    if (federation.getSuperUserPassword() != null) {
      currentFederation.setSuperUserPassword(federation.getSuperUserPassword());
    }
    return client.put(currentFederation.getSelfUri(), Federation.class, currentFederation);
  }

  private void fillDefaults() {
    if (federation.getBootstrap() == null) {
      federation.setBootstrap("xhive://127.0.0.1:2910");
    }
    if (federation.getSuperUserPassword() == null) {
      federation.setSuperUserPassword("test");
    }
  }

  public boolean equals(Object other) {
    if (!(other instanceof FederationIaHandler)) {
      return false;
    }
    FederationIaHandler handler = (FederationIaHandler)other;
    return Objects.equals(federation.getName(), handler.federation.getName())
        && Objects.equals(federation.getBootstrap(), handler.federation.getBootstrap())
        && Objects.equals(federation.getSuperUserPassword(), handler.federation.getSuperUserPassword());
  }

  public int hashCode() {
    return Objects.hash(federation.getName(), federation.getBootstrap(), federation.getSuperUserPassword());
  }

  private static final class FederationExtractor extends ArtifactExtractor {
    @Override
    public FederationIaHandler extract(Object representation) {
      Map federationRepresentation = asMap(representation);
      Federation federation = new Federation();
      federation.setName(extractName(federationRepresentation));
      federation.setBootstrap(extractString(federationRepresentation, "bootstrap"));
      federation.setSuperUserPassword(extractString(federationRepresentation, "superUserPassword"));
      return new FederationIaHandler(federation);
    }

    @Override
    public String getFieldName() {
      return "federation";
    }
  }
}
