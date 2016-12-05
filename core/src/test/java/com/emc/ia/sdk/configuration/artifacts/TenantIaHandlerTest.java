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

  @Test
  public void whenExtractingTenantFromConfiguration() {
    ArtifactExtractor tenantExtractor = TenantIaHandler.extractor();
    BaseIAArtifact tenantArtifact = tenantExtractor.extract("MY_COMPANY");
    BaseIAArtifact expectedArtifact = new TenantIaHandler("MY_COMPANY");
    assertEquals("Extracted artifact should be equal to expected", expectedArtifact, tenantArtifact);
  }

  @Test
  public void whenInstallingArtifact() throws IOException {
    BaseIAArtifact tenantArtifact = new TenantIaHandler("MY_COMPANY");
    IACache cache = new IACache();
    cache.cacheOne(new Services());
    RestClient client = mock(RestClient.class);
    Tenants retTenants = mock(Tenants.class);
    Tenant resultTenant = new Tenant();
    resultTenant.setName("MY_COMPANY");
    when(client.follow(any(), any(), eq(Tenants.class))).thenReturn(retTenants);
    when(retTenants.byName("MY_COMPANY")).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultTenant);

    tenantArtifact.install(client, cache);

    verify(client).createCollectionItem(eq(retTenants), any(), eq(LINK_ADD), eq(LINK_SELF));
  }

}
