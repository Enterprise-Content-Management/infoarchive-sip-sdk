package com.emc.ia.sdk.configuration.artifacts;


import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;
import java.util.Map;

public final class ApplicationIaHandler extends BaseIAArtifact {

  public static Extractor extractor() {
    return new ApplicationExtractor();
  }

  private final Application application;

  public ApplicationIaHandler(Application source) {
    this.application = source;
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

  private static final class ApplicationExtractor extends ArtifactExtractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      Map appRepresentation = asMap(representation);
      Application application = new Application();
      application.setName(extractString(appRepresentation, "name"));
      application.setArchiveType(extractString(appRepresentation, "archiveType"));
      application.setType(extractString(appRepresentation, "type"));
      return new ApplicationIaHandler(application);
    }

    @Override
    public String getFieldName() {
      return "application";
    }
  }
}
