/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import static org.junit.Assert.assertEquals;
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

  @Test
  public void whenExtractingFederation() {
    ArtifactExtractor federationExtractor = FederationIaHandler.extractor();
    BaseIAArtifact federationArtifact = federationExtractor.extract(getAppRepresentation());
    BaseIAArtifact expectedArtifact = getFederationSample();
    assertEquals("Extracted artifact should be equal to expected", expectedArtifact, federationArtifact);
  }

  @Test
  public void whenInstallingFederation() throws IOException {
    BaseIAArtifact federationArtifact = getFederationSample();
    IACache cache = new IACache();
    cache.cacheOne(new Services());
    RestClient client = mock(RestClient.class);
    Federations federations = mock(Federations.class);
    Federation resultFederation = new Federation();
    resultFederation.setName(FED_NAME);
    when(client.follow(any(), any(), eq(Federations.class))).thenReturn(federations);
    when(federations.byName(FED_NAME)).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultFederation);

    federationArtifact.install(client, cache);

    verify(client).createCollectionItem(eq(federations), any(), eq(LINK_ADD), eq(LINK_SELF));
  }

  private FederationIaHandler getFederationSample() {
    Federation federation = new Federation();
    federation.setName(FED_NAME);
    federation.setSuperUserPassword("test");
    federation.setBootstrap("xhive://127.0.0.1:2910");
    return new FederationIaHandler(federation);
  }

  private Map<String, String> getAppRepresentation() {
    Map<String, String> federationMap = new HashMap<>();
    federationMap.put("name", FED_NAME);
    federationMap.put("bootstrap", "xhive://127.0.0.1:2910");
    federationMap.put("superUserPassword", "test");
    return federationMap;
  }

}
