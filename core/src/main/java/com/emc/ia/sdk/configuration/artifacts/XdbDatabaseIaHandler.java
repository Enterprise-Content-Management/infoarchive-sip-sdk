package com.emc.ia.sdk.configuration.artifacts;


import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Databases;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;
import java.util.Map;

public final class XdbDatabaseIaHandler extends BaseIAArtifact {

  public static Extractor extractor() {
    return new XdbDatabaseExtractor();
  }

  private final Database xdbDatabase;

  private XdbDatabaseIaHandler(Database source) {
    this.xdbDatabase = source;
  }

  @Override
  public void install(RestClient client, IACache cache) throws IOException {
    Databases databases = client.follow(cache.getFirst(Federation.class), LINK_DATABASES, Databases.class);
    Database createdDb = databases.byName(xdbDatabase.getName());
    if (createdDb == null) {
      createdDb = client.createCollectionItem(databases, xdbDatabase, LINK_ADD, LINK_SELF);
    }
    cache.cacheOne(createdDb);
  }

  private static final class XdbDatabaseExtractor implements Extractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      Map databaseRepresentation = (Map) representation;
      Database db = new Database();
      db.setName((String) databaseRepresentation.get("name"));
      db.setAdminPassword((String) databaseRepresentation.get("adminPassword"));
      return new XdbDatabaseIaHandler(db);

    }

    @Override
    public String getFieldName() {
      return "xdbDatabase";
    }
  }
}
