/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_ADD;
import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_SPACES;
import static com.emc.ia.sdk.support.rest.StandardLinkRelations.LINK_SELF;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Space;
import com.emc.ia.sdk.sip.client.dto.Spaces;
import com.emc.ia.sdk.support.rest.RestClient;

public class SpaceIaHandlerTest {
  private static final String SPACE_NAME = "Application-space";
  private IACache cache;
  private RestClient client;

  @Before
  public void setUp() {
    cache = new IACache();
    Application application = new Application();
    application.setName("Application");
    cache.cacheOne(application);
    client = mock(RestClient.class);
  }

  @Test
  public void whenExtractingSpaceFromConfiguration() {
    ArtifactExtractor spaceExtractor = SpaceIaHandler.extractor();
    BaseIAArtifact spaceArtifact = spaceExtractor.extract(SPACE_NAME);
    BaseIAArtifact expectedArtifact = new SpaceIaHandler(SPACE_NAME);
    assertEquals("Extracted artifact should be equal to expected", expectedArtifact, spaceArtifact);
  }

  @Test
  public void whenInstallingArtifact() throws IOException {
    Space resultSpace = setUpInstallation();

    new SpaceIaHandler(SPACE_NAME).install(client, cache);

    assertEquals(resultSpace, cache.getFirst(Space.class));
  }

  @Test
  public void whenInstallingDefaultSpace() throws IOException {
    Space resultSpace = setUpInstallation();

    new SpaceIaHandler().install(client, cache);

    assertEquals(resultSpace, cache.getFirst(Space.class));
  }

  @Test
  public void whenInstallingExistingSpace() throws IOException {
    Spaces spaces = mock(Spaces.class);
    Space existingSpace = new Space();
    existingSpace.setName(SPACE_NAME);
    when(client.follow(any(), eq(LINK_SPACES), eq(Spaces.class))).thenReturn(spaces);
    when(spaces.byName(SPACE_NAME)).thenReturn(existingSpace);

    new SpaceIaHandler(SPACE_NAME).install(client, cache);

    assertEquals(existingSpace, cache.getFirst(Space.class));
  }

  @Test(expected = IllegalStateException.class)
  public void whenInstallingInWrongOrder() throws IOException {
    IACache emptyCache = new IACache();

    new SpaceIaHandler(SPACE_NAME).install(client, emptyCache);
  }

  private Space setUpInstallation() throws IOException {
    Spaces spaces = mock(Spaces.class);
    Space resultSpace = new Space();
    resultSpace.setName(SPACE_NAME);
    when(client.follow(any(), eq(LINK_SPACES), eq(Spaces.class))).thenReturn(spaces);
    when(spaces.byName(SPACE_NAME)).thenReturn(null);
    when(client.createCollectionItem(eq(spaces), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultSpace);
    return resultSpace;
  }
}

