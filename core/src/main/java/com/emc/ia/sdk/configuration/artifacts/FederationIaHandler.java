package com.emc.ia.sdk.configuration.artifacts;


import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;
import java.util.Map;

public final class FederationIaHandler extends BaseIAArtifact {

  public static Extractor extractor() {
    return new FederationExtractor();
  }

  private final Federation federation;

  private FederationIaHandler(Federation source) {
    this.federation = source;
  }

  @Override
  public void install(RestClient client, IACache cache) throws IOException {
    Federations federations = client.follow(cache.getFirst(Services.class), LINK_FEDERATIONS, Federations.class);
    Federation createdFederation = federations.byName(federation.getName());
    if (createdFederation == null) {
      createdFederation = client.createCollectionItem(federations, federation, LINK_ADD, LINK_SELF);
    }
    cache.cacheOne(createdFederation);
  }

  private static final class FederationExtractor implements Extractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      Map federationRepresentation = (Map) representation;
      Federation federation = new Federation();
      federation.setName((String) federationRepresentation.get("name"));
      federation.setBootstrap((String) federationRepresentation.get("bootstrap"));
      federation.setSuperUserPassword((String) federationRepresentation.get("superUserPassword"));
      return new FederationIaHandler(federation);
    }

    @Override
    public String getFieldName() {
      return "federation";
    }
  }
}
