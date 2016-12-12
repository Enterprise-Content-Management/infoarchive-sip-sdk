/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;
import java.util.Objects;

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

  private final String name;

  public SpaceIaHandler() {
    name = null;
  }

  public SpaceIaHandler(String name) {
    this.name = Objects.requireNonNull(name);
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Application application = cache.getFirst(Application.class);
    if (application == null) {
      throw new IllegalStateException("Wrong installation order: application is not installed yet");
    }
    Spaces spaces = client.follow(application, LINK_SPACES, Spaces.class);
    String spaceName = fillName(application.getName());
    Space createdSpace = spaces.byName(spaceName);
    if (createdSpace == null) {
      createdSpace = client.createCollectionItem(spaces, buildSpace(spaceName), LINK_ADD, LINK_SELF);
    }
    cache.cacheOne(createdSpace);
  }

  private String fillName(String applicationName) {
    if (name == null) {
      return applicationName + "-space";
    } else {
      return name;
    }
  }

  private Space buildSpace(String spaceName) {
    Space space = new Space();
    space.setName(spaceName);
    return space;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof SpaceIaHandler)) {
      return false;
    }
    SpaceIaHandler handler = (SpaceIaHandler)other;
    return Objects.equals(name, handler.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  private static final class SpaceExtractor extends ArtifactExtractor {
    @Override
    public SpaceIaHandler extract(Object representation) {
      return new SpaceIaHandler(asString(representation));
    }

    @Override
    public String getFieldName() {
      return "space";
    }
  }
}
