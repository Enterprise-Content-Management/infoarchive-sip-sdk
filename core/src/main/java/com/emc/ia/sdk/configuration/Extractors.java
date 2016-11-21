package com.emc.ia.sdk.configuration;

import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Tenant;

import java.util.Map;

public class Extractors {

  private Extractors(){}

  public static Extractor<Tenant> getTenantExtractor() {
    return representation -> {
      String tenantName = (String) representation;
      Tenant targetTenant = new Tenant();
      targetTenant.setName(tenantName);
      return targetTenant;
    };
  }

  public static Extractor<Application> getApplicationExtractor() {
    return representation -> {
      Map appRepresentation = (Map) representation;
      Application application = new Application();
      application.setName((String) appRepresentation.get("name"));
      application.setArchiveType((String) appRepresentation.get("archiveType"));
      application.setType((String) appRepresentation.get("type"));
      return application;
    };
  }

  public static Extractor<Federation> getFederationExtractor() {
    return representation -> {
      Map federationRepresentation = (Map) representation;
      Federation federation = new Federation();
      federation.setName((String) federationRepresentation.get("name"));
      federation.setBootstrap((String) federationRepresentation.get("bootstrap"));
      federation.setSuperUserPassword((String) federationRepresentation.get("superUserPassword"));
      return federation;
    };
  }

  public static Extractor<Database> getDatabaseExtractor() {
    return representation -> {
      Map databaseRepresentation = (Map) representation;
      Database db = new Database();
      db.setName((String) databaseRepresentation.get("name"));
      db.setAdminPassword((String) databaseRepresentation.get("adminPassword"));
      return db;
    };
  }

}
