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
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Databases;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.support.rest.RestClient;

public final class XdbDatabaseIaHandler extends BaseIAArtifact {

  public static XdbDatabaseExtractor extractor() {
    return new XdbDatabaseExtractor();
  }

  private final String name;
  private final String adminPassword;

  public XdbDatabaseIaHandler(String name, String adminPassword) {
    this.name = Optional.ofNullable(name).orElse("AIP-xdb");
    this.adminPassword = Optional.ofNullable(adminPassword).orElse("secret");
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Databases databases = client.follow(cache.getFirst(Federation.class), LINK_DATABASES, Databases.class);
    Database createdDb = databases.byName(name);
    if (createdDb == null) {
      createdDb = client.createCollectionItem(databases, buildDatabase(), LINK_ADD, LINK_SELF);
    } else {
      ensureDatabase(createdDb);
    }
    cache.cacheOne(createdDb);
  }

  @Override
  public int getInstallationOrderKey() {
    return 4;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof XdbDatabaseIaHandler)) {
      return false;
    }
    XdbDatabaseIaHandler handler = (XdbDatabaseIaHandler)other;
    return name.equals(handler.name)
        && adminPassword.equals(handler.adminPassword);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, adminPassword);
  }

  private Database buildDatabase() {
    Database xdbDatabase = new Database();
    xdbDatabase.setName(name);
    xdbDatabase.setAdminPassword(adminPassword);
    return xdbDatabase;
  }

  private void ensureDatabase(Database database) {
    if (!adminPassword.equals(database.getAdminPassword())) {
      throw new IllegalStateException("Current xdbDatabase has different admin password than configured");
    }
  }

  private static final class XdbDatabaseExtractor extends ArtifactExtractor {
    @Override
    public XdbDatabaseIaHandler extract(Object representation) {
      Map<String, Object> databaseRepresentation = asMap(representation);
      String name = extractName(databaseRepresentation);
      String adminPassword = extractString(databaseRepresentation, "adminPassword");
      return new XdbDatabaseIaHandler(name, adminPassword);
    }

    @Override
    public String getFieldName() {
      return "xdbDatabase";
    }
  }
}
