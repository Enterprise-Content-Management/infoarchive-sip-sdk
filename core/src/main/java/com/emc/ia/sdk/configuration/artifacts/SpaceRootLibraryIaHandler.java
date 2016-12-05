/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;
import java.util.Map;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.SpaceRootLibraries;
import com.emc.ia.sdk.sip.client.dto.SpaceRootLibrary;
import com.emc.ia.sdk.support.rest.RestClient;

public final class SpaceRootLibraryIaHandler extends BaseIAArtifact {

  public static SpaceRootLibraryExtractor extractor() {
    return new SpaceRootLibraryExtractor();
  }

  private final SpaceRootLibrary srLibrary;
  private final String parentSpaceName;
  private final String xdbDatabaseName;

  public SpaceRootLibraryIaHandler(SpaceRootLibrary source, String parentSpaceName, String xdbDatabaseName) {
    this.srLibrary = source;
    this.parentSpaceName = parentSpaceName;
    this.xdbDatabaseName = xdbDatabaseName;
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    SpaceRootLibraries libraries = client.follow(cache.getByClassWithName(Space.class, parentSpaceName), // What if there is no space with such name ???
        LINK_SPACE_ROOT_LIBRARIES, SpaceRootLibraries.class);
    SpaceRootLibrary createdLibrary = libraries.byName(srLibrary.getName());
    if (createdLibrary == null) {
      String databaseUri = cache.getByClassWithName(Database.class, xdbDatabaseName).getSelfUri(); // What if there is no database with such name ???
      srLibrary.setXdbDatabase(databaseUri);
      createdLibrary = client.createCollectionItem(libraries, srLibrary, LINK_ADD, LINK_SELF);
    }
    // TODO: Update existing library if needed
    cache.cacheOne(createdLibrary);
  }

  private static final class SpaceRootLibraryExtractor extends ArtifactExtractor {
    @Override
    public SpaceRootLibraryIaHandler extract(Object representation) {
      Map srLibraryRepresentation = asMap(representation);
      SpaceRootLibrary srLibrary = new SpaceRootLibrary();
      srLibrary.setName(extractString(srLibraryRepresentation, "name"));
      String parentSpaceName = extractString(srLibraryRepresentation, "space");
      String xdbDatabaseName = extractString(srLibraryRepresentation, "xdbDatabase");
      return new SpaceRootLibraryIaHandler(srLibrary, parentSpaceName, xdbDatabaseName);
    }

    @Override
    public String getFieldName() {
      return "spaceRootLibrary";
    }
  }

}

