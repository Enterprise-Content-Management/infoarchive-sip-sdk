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
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.support.rest.RestClient;

public class ApplicationIaHandlerTest {

  private static final String APP_NAME = "PhoneCalls";
  private static final String ARCH_TYPE = "SIP";
  private static final String TYPE = "ACTIVE_ARCHIVING";
  private static final String CATEGORY = "Customer Support";
  private IACache cache;
  private RestClient client;
  private Applications applications;

  @Before
  public void setUp() {
    cache = new IACache();
    client = mock(RestClient.class);
    applications = mock(Applications.class);
    cache.cacheOne(new Tenant());
  }

  @Test
  public void whenComparingNonEquals() {
    assertNotEquals(getApplicationArtifact(), null);
    assertNotEquals(getApplicationArtifact(),
        new ApplicationIaHandler("WrongName", ARCH_TYPE, TYPE, CATEGORY, null));
    assertNotEquals(getApplicationArtifact(),
        new ApplicationIaHandler(APP_NAME, "WrongArchiveType", TYPE, CATEGORY, null));
    assertNotEquals(getApplicationArtifact(),
        new ApplicationIaHandler(APP_NAME, ARCH_TYPE, "WrongType", CATEGORY, null));
    assertNotEquals(getApplicationArtifact(),
        new ApplicationIaHandler(APP_NAME, ARCH_TYPE, TYPE, "WrongCategory", null));
    assertNotEquals(getApplicationArtifact(),
        new ApplicationIaHandler(APP_NAME, ARCH_TYPE, TYPE, CATEGORY, "WrongDescription"));
  }

  @Test
  public void whenExtractingApplication() {
    ArtifactExtractor appExtractor = ApplicationIaHandler.extractor();
    BaseIAArtifact appArtifact = appExtractor.extract(getAppRepresentation());
    BaseIAArtifact expectedArtifact = getApplicationArtifact();
    assertEquals("Extracted application should be equal to expected", expectedArtifact, appArtifact);
  }

  @Test
  public void whenInstallingApplication() throws IOException {
    Application resultApp = getApplication();
    when(client.follow(any(), any(), eq(Applications.class))).thenReturn(applications);
    when(applications.byName(APP_NAME)).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultApp);

    getApplicationArtifact().install(client, cache);

    verify(client).createCollectionItem(eq(applications), any(), eq(LINK_ADD), eq(LINK_SELF));
  }

  @Test
  public void whenInstallingExistingApplication() throws IOException {
    Application existingApp = getApplication();
    when(client.follow(any(), any(), eq(Applications.class))).thenReturn(applications);
    when(applications.byName(APP_NAME)).thenReturn(existingApp);

    getApplicationArtifact().install(client, cache);

    assertEquals("Cached app should be equal to existing",
        existingApp, cache.getByClassWithName(Application.class, APP_NAME));
  }



  private ApplicationIaHandler getApplicationArtifact() {
    return new ApplicationIaHandler(APP_NAME, ARCH_TYPE, TYPE, CATEGORY, null);
  }

  private Application getApplication() {
    Application application = new Application();
    application.setName(APP_NAME);
    application.setCategory("Customer Support");
    return application;
  }

  private Map<String, String> getAppRepresentation() {
    Map<String, String> appMap = new HashMap<>();
    appMap.put("name", APP_NAME);
    appMap.put("archiveType", "SIP");
    appMap.put("type", "ACTIVE_ARCHIVING");
    appMap.put("category", "Customer Support");
    return appMap;
  }
}
