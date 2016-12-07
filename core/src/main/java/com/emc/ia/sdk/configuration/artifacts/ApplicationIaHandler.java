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
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.support.rest.RestClient;

public final class ApplicationIaHandler extends BaseIAArtifact {

  public static ApplicationExtractor extractor() {
    return new ApplicationExtractor();
  }

  private final Application application;

  public ApplicationIaHandler(Application source) {
    this.application = new Application();
    application.setName(source.getName());
    application.setArchiveType(source.getArchiveType());
    application.setType(source.getType());
    application.setCategory(source.getCategory());
    application.setDescription(source.getDescription());
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Applications applications = client.follow(cache.getFirst(Tenant.class), LINK_APPLICATIONS, Applications.class);
    Application createdApplication = applications.byName(application.getName());
    if (createdApplication == null) {
      createdApplication = client.createCollectionItem(applications, application, LINK_ADD, LINK_SELF);
    }
    cache.cacheOne(createdApplication);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof ApplicationIaHandler)) {
      return false;
    }
    ApplicationIaHandler handler = (ApplicationIaHandler)other;
    return Objects.equals(application.getName(), handler.application.getName())
        && Objects.equals(application.getArchiveType(), handler.application.getArchiveType())
        && Objects.equals(application.getType(), handler.application.getType())
        && Objects.equals(application.getCategory(), handler.application.getCategory())
        && Objects.equals(application.getDescription(), handler.application.getDescription());
  }

  @Override
  public int hashCode() {
    return Objects.hash(application.getName(), application.getArchiveType(), application.getType(),
        application.getCategory(), application.getDescription());
  }

  private static final class ApplicationExtractor extends ArtifactExtractor {
    @Override
    public ApplicationIaHandler extract(Object representation) {
      Map appRepresentation = asMap(representation);
      Application application = new Application();
      application.setName(extractName(appRepresentation));
      application.setArchiveType(extractString(appRepresentation, "archiveType"));
      application.setType(extractString(appRepresentation, "type"));
      application.setCategory(extractString(appRepresentation, "category"));
      application.setDescription(extractString(appRepresentation, "description"));
      return new ApplicationIaHandler(application);
    }

    @Override
    public String getFieldName() {
      return "application";
    }
  }
}
