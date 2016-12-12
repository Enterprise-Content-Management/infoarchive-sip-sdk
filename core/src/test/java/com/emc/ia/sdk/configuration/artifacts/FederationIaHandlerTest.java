/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_ADD;
import static com.emc.ia.sdk.support.rest.StandardLinkRelations.LINK_SELF;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Federation;
import com.emc.ia.sdk.sip.client.dto.Federations;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.support.rest.RestClient;

public class FederationIaHandlerTest {

  private static final String FED_NAME = "mainFederation";
  private static final String FED_BOOTSTRAP = "xhive://127.0.0.1:2910";
  private static final String FED_PASSWORD = "test";
  private IACache cache;
  private RestClient client;
  private Federations federations;

  @Before
  public void setUp() {
    cache = new IACache();
    cache.cacheOne(new Services());
    client = mock(RestClient.class);
    federations = mock(Federations.class);
  }

  @Test
  public void whenComparingNonEquals() {
    assertNotEquals(getFederationArtifactSample(), null);
    assertNotEquals(getFederationArtifactSample(),
        new FederationIaHandler("wrongName", FED_BOOTSTRAP, FED_PASSWORD));
    assertNotEquals(getFederationArtifactSample(),
        new FederationIaHandler(FED_NAME, "wrongBootstrap", FED_PASSWORD));
    assertNotEquals(getFederationArtifactSample(),
        new FederationIaHandler(FED_NAME, FED_BOOTSTRAP, "wrongPassword"));
  }

  @Test
  public void whenExtractingFederation() {
    ArtifactExtractor federationExtractor = FederationIaHandler.extractor();
    BaseIAArtifact federationArtifact = federationExtractor.extract(getFederationRepresentation());
    BaseIAArtifact expectedArtifact = getFederationArtifactSample();
    assertEquals("Extracted artifact should be equal to expected", expectedArtifact, federationArtifact);
  }

  @Test
  public void whenInstallingFederation() throws IOException {
    BaseIAArtifact federationArtifact = getFederationArtifactSample();
    Federation resultFederation = new Federation();
    resultFederation.setName(FED_NAME);
    when(client.follow(any(), any(), eq(Federations.class))).thenReturn(federations);
    when(federations.byName(FED_NAME)).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultFederation);

    federationArtifact.install(client, cache);

    verify(client).createCollectionItem(eq(federations), any(), eq(LINK_ADD), eq(LINK_SELF));
  }

  @Test
  public void whenInstallingExistingFederation() throws IOException {
    BaseIAArtifact federationArtifact = getFederationArtifactSample();
    Federation resultFederation = new Federation();
    resultFederation.setName(FED_NAME);
    resultFederation.setBootstrap(FED_BOOTSTRAP);
    resultFederation.setSuperUserPassword(FED_PASSWORD);
    when(client.follow(any(), any(), eq(Federations.class))).thenReturn(federations);
    when(federations.byName(FED_NAME)).thenReturn(resultFederation);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultFederation);

    federationArtifact.install(client, cache);

    assertEquals(resultFederation, cache.getByClassWithName(Federation.class, FED_NAME));
  }

  @Test(expected = IllegalStateException.class)
  public void whenInstallingWrongFederation() throws IOException {
    BaseIAArtifact federationArtifact = new FederationIaHandler(FED_NAME, "wrongBootstrap", FED_PASSWORD);
    Federation resultFederation = getFederationSample();
    when(client.follow(any(), any(), eq(Federations.class))).thenReturn(federations);
    when(federations.byName(FED_NAME)).thenReturn(resultFederation);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultFederation);

    federationArtifact.install(client, cache);

    assertEquals(resultFederation, cache.getByClassWithName(Federation.class, FED_NAME));
  }

  private Federation getFederationSample() {
    Federation federation = new Federation();
    federation.setName(FED_NAME);
    federation.setBootstrap(FED_BOOTSTRAP);
    federation.setSuperUserPassword(FED_PASSWORD);
    return federation;
  }

  private FederationIaHandler getFederationArtifactSample() {
    return new FederationIaHandler(FED_NAME, FED_BOOTSTRAP, FED_PASSWORD);
  }

  private Map<String, String> getFederationRepresentation() {
    Map<String, String> federationMap = new HashMap<>();
    federationMap.put("name", FED_NAME);
    federationMap.put("bootstrap", FED_BOOTSTRAP);
    federationMap.put("superUserPassword", FED_PASSWORD);
    return federationMap;
  }

}
