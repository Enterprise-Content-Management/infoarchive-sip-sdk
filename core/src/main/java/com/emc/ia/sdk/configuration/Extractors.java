package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Tenant;

import java.util.Map;

public class Extractors {

  private Extractors(){}

  public static Extractor<Tenant> getTenantExtractor() {
    return (k, config) -> {
      String tenantName = (String) config.get(k);
      Tenant targetTenant = new Tenant();
      targetTenant.setName(tenantName);
      return targetTenant;
    };
  }

  public static Extractor<Application> getApplicationExtractor() {
    return (k, config) -> {
      Map appRepresentation = (Map) config.get(k);
      Application application = new Application();
      application.setName((String) appRepresentation.get("name"));
      application.setArchiveType((String) appRepresentation.get("archiveType"));
      application.setType((String) appRepresentation.get("type"));
      return application;
    };
  }

  public static Extractor<Federation> getFederationExtractor() {
    return (k, config) -> {
      Map federationRepresentation = (Map) config.get(k);
      Federation federation = new Federation();
      federation.setName((String) federationRepresentation.get("name"));
      federation.setBootstrap((String) federationRepresentation.get("bootstrap"));
      federation.setSuperUserPassword((String) federationRepresentation.get("superUserPassword"));
      return federation;
    };
  }

}
