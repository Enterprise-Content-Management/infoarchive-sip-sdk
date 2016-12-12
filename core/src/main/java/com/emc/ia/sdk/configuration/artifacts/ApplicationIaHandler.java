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
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.support.rest.RestClient;

public final class ApplicationIaHandler extends BaseIAArtifact {

  public static ApplicationExtractor extractor() {
    return new ApplicationExtractor();
  }

  private final String name;
  private final String archiveType;
  private final String type;
  private final String category;
  private final String description;

  public ApplicationIaHandler(String name, String archiveType, String type, String category, String description) {
    this.name = Objects.requireNonNull(name, "Application name should not be null");
    this.archiveType = Optional.ofNullable(archiveType).orElse("SIP");
    this.type = Optional.ofNullable(type).orElse("ACTIVE_ARCHIVING");
    this.category = category;
    this.description = description;
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Applications applications = client.follow(cache.getFirst(Tenant.class), LINK_APPLICATIONS, Applications.class);
    Application createdApplication = applications.byName(name);
    if (createdApplication == null) {
      createdApplication = client.createCollectionItem(applications, buildApplication(), LINK_ADD, LINK_SELF);
    }
    cache.cacheOne(createdApplication);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof ApplicationIaHandler)) {
      return false;
    }
    ApplicationIaHandler handler = (ApplicationIaHandler)other;
    return Objects.equals(name, handler.name)
        && Objects.equals(archiveType, handler.archiveType)
        && Objects.equals(type, handler.type)
        && Objects.equals(category, handler.category)
        && Objects.equals(description, handler.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, archiveType, type, category, description);
  }

  private Application buildApplication() {
    Application application = new Application();
    application.setName(name);
    application.setArchiveType(archiveType);
    application.setType(type);
    application.setCategory(category);
    application.setDescription(description);
    return application;
  }

  private static final class ApplicationExtractor extends ArtifactExtractor {
    @Override
    public ApplicationIaHandler extract(Object representation) {
      Map appRepresentation = asMap(representation);
      String name = extractName(appRepresentation);
      String archiveType = extractString(appRepresentation, "archiveType");
      String type = extractString(appRepresentation, "type");
      String category = extractString(appRepresentation, "category");
      String description = extractString(appRepresentation, "description");
      return new ApplicationIaHandler(name, archiveType, type, category, description);
    }

    @Override
    public String getFieldName() {
      return "application";
    }
  }
}
