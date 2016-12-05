/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.Spaces;
import com.emc.ia.sdk.support.rest.RestClient;

public final class SpaceIaHandler extends BaseIAArtifact {

  public static SpaceExtractor extractor() {
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
    public SpaceIaHandler extract(Object representation) {
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
