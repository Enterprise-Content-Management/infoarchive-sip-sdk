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

import org.junit.Test;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.sip.client.dto.Tenants;
import com.emc.ia.sdk.support.rest.RestClient;

public class TenantIaHandlerTest {

  private static final String TEN_NAME = "MY_COMPANY";

  @Test
  public void whenExtractingTenantFromConfiguration() {
    ArtifactExtractor tenantExtractor = TenantIaHandler.extractor();
    BaseIAArtifact tenantArtifact = tenantExtractor.extract(TEN_NAME);
    BaseIAArtifact expectedArtifact = new TenantIaHandler(TEN_NAME);
    assertEquals("Extracted artifact should be equal to expected", expectedArtifact, tenantArtifact);
  }

  @Test
  public void whenInstallingArtifact() throws IOException {
    BaseIAArtifact tenantArtifact = new TenantIaHandler(TEN_NAME);
    IACache cache = new IACache();
    cache.cacheOne(new Services());
    RestClient client = mock(RestClient.class);
    Tenants retTenants = mock(Tenants.class);
    Tenant resultTenant = new Tenant();
    resultTenant.setName(TEN_NAME);
    when(client.follow(any(), any(), eq(Tenants.class))).thenReturn(retTenants);
    when(retTenants.byName(TEN_NAME)).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultTenant);

    tenantArtifact.install(client, cache);

    verify(client).createCollectionItem(eq(retTenants), any(), eq(LINK_ADD), eq(LINK_SELF));
  }

}
