/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_ADD;
import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_DATABASES;
import static com.emc.ia.sdk.support.rest.StandardLinkRelations.LINK_SELF;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Database;
import com.emc.ia.sdk.sip.client.dto.Databases;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.support.rest.RestClient;

public class XdbDatabaseIaHandlerTest {

  private static final String DB_NAME = "AIP-xdb";
  private static final String PASSWORD = "secret";
  private IACache cache;
  private RestClient client;
  private Databases databases;

  @Before
  public void setUp() {
    cache = new IACache();
    cache.cacheOne(new Federation());
    client = mock(RestClient.class);
    databases = mock(Databases.class);
  }

  @Test
  public void whenComparingNotEqualDatabases() {
    assertNotEquals(getDatabaseArtifactSample(), null);
    assertNotEquals(getDatabaseArtifactSample(),
        new XdbDatabaseIaHandler("WrongName", PASSWORD));
    assertNotEquals(getDatabaseArtifactSample(),
        new XdbDatabaseIaHandler(DB_NAME, "WrongPassword"));
  }

  @Test
  public void whenExtractingDatabase() {
    ArtifactExtractor xdbDatabaseExtractor = XdbDatabaseIaHandler.extractor();
    BaseIAArtifact databaseArtifact = xdbDatabaseExtractor.extract(getDatabaseRepresentation());
    BaseIAArtifact expectedArtifact = getDatabaseArtifactSample();
    assertEquals("Expected artifact should be equal to extracted", expectedArtifact, databaseArtifact);
  }

  @Test
  public void whenInstallingDatabase() throws IOException {
    Database resultDatabase = new Database();
    resultDatabase.setName(DB_NAME);
    when(client.follow(any(), eq(LINK_DATABASES), eq(Databases.class))).thenReturn(databases);
    when(databases.byName(eq(DB_NAME))).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultDatabase);

    getDatabaseArtifactSample().install(client, cache);

    assertEquals(resultDatabase, cache.getFirst(Database.class));
  }

  @Test
  public void whenInstallingExistingDatabase() throws IOException {
    Database existingDatabase = new Database();
    existingDatabase.setName(DB_NAME);
    existingDatabase.setAdminPassword(PASSWORD);
    when(client.follow(any(), eq(LINK_DATABASES), eq(Databases.class))).thenReturn(databases);
    when(databases.byName(eq(DB_NAME))).thenReturn(existingDatabase);

    getDatabaseArtifactSample().install(client, cache);

    assertEquals(existingDatabase, cache.getFirst(Database.class));
  }

  @Test(expected = IllegalStateException.class)
  public void whenInstallingWithWrongPassword() throws IOException {
    Database existingDatabase = new Database();
    existingDatabase.setName(DB_NAME);
    existingDatabase.setAdminPassword("AnotherPassword");
    when(client.follow(any(), eq(LINK_DATABASES), eq(Databases.class))).thenReturn(databases);
    when(databases.byName(eq(DB_NAME))).thenReturn(existingDatabase);

    getDatabaseArtifactSample().install(client, cache);
  }


  private Map<String, String> getDatabaseRepresentation() {
    Map<String, String> representation = new HashMap<>();
    representation.put("name", DB_NAME);
    representation.put("adminPassword", PASSWORD);
    return representation;
  }

  private XdbDatabaseIaHandler getDatabaseArtifactSample() {
    return new XdbDatabaseIaHandler(DB_NAME, PASSWORD);
  }
}
