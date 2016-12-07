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
import com.emc.ia.sdk.sip.client.dto.Application;
import com.emc.ia.sdk.sip.client.dto.Applications;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.support.rest.RestClient;

public class ApplicationIaHandlerTest {

  private static final String APP_NAME = "PhoneCalls";

  @Test
  public void whenExtractingApplication() {
    ArtifactExtractor appExtractor = ApplicationIaHandler.extractor();
    BaseIAArtifact appArtifact = appExtractor.extract(getAppRepresentation());
    BaseIAArtifact expectedArtifact = getApplicationArtifact();
    assertEquals("Extracted application should be equal to expected", expectedArtifact, appArtifact);
  }

  @Test
  public void whenInstallingApplication() throws IOException {
    BaseIAArtifact appArtifact = getApplicationArtifact();
    IACache cache = new IACache();
    cache.cacheOne(new Tenant());
    RestClient client = mock(RestClient.class);
    Applications applications = mock(Applications.class);
    Application resultApp = new Application();
    resultApp.setName(APP_NAME);
    when(client.follow(any(), any(), eq(Applications.class))).thenReturn(applications);
    when(applications.byName(APP_NAME)).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultApp);

    appArtifact.install(client, cache);

    verify(client).createCollectionItem(eq(applications), any(), eq(LINK_ADD), eq(LINK_SELF));
  }

  private ApplicationIaHandler getApplicationArtifact() {
    Application resultApplication = new Application();
    resultApplication.setName(APP_NAME);
    resultApplication.setArchiveType("SIP");
    resultApplication.setType("ACTIVE_ARCHIVING");
    resultApplication.setCategory("Customer Support");
    return new ApplicationIaHandler(resultApplication);
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
