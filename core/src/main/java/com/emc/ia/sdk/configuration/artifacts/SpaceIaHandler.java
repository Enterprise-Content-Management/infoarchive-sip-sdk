package com.emc.ia.sdk.configuration.artifacts;


import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.Spaces;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public final class SpaceIaHandler extends BaseIAArtifact {

  public static Extractor extractor() {
    return new SpaceExtractor();
  }

  private final Space space;

  public SpaceIaHandler(Space source) {
    this.space = source;
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Spaces spaces = client.follow(cache.getFirst(Application.class), LINK_SPACES, Spaces.class);
    Space createdSpace = spaces.byName(space.getName());
    if (createdSpace == null) {
      createdSpace = client.createCollectionItem(spaces, space, LINK_ADD, LINK_SELF);
    }
    cache.cacheOne(createdSpace);
  }

  private static final class SpaceExtractor extends ArtifactExtractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      String spaceName = asString(representation);
      Space space = new Space();
      space.setName(spaceName);
      return new SpaceIaHandler(space);
    }

    @Override
    public String getFieldName() {
      return "space";
    }
  }
}
