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
import static com.emc.ia.sdk.sip.client.rest.InfoArchiveLinkRelations.LINK_TENANT;
import static com.emc.ia.sdk.support.rest.StandardLinkRelations.LINK_SELF;

import java.io.IOException;

import org.junit.Before;
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
  private IACache cache;
  private RestClient client;

  @Before
  public void setUp() {
    cache = new IACache();
    client = mock(RestClient.class);
    cache.cacheOne(new Services());
  }

  @Test
  public void whenExtractingTenantFromConfiguration() {
    ArtifactExtractor tenantExtractor = TenantIaHandler.extractor();
    BaseIAArtifact tenantArtifact = tenantExtractor.extract(TEN_NAME);
    BaseIAArtifact expectedArtifact = new TenantIaHandler(TEN_NAME);
    assertEquals("Extracted artifact should be equal to expected", expectedArtifact, tenantArtifact);
  }

  @Test
  public void whenInstallingExistingTenant() throws IOException {
    BaseIAArtifact tenantArtifact = new TenantIaHandler(TEN_NAME);
    Tenants tenants = mock(Tenants.class);
    Tenant existingTenant = new Tenant();
    existingTenant.setName(TEN_NAME);
    when(client.follow(any(), any(), eq(Tenants.class))).thenReturn(tenants);
    when(tenants.byName(TEN_NAME)).thenReturn(existingTenant);

    tenantArtifact.install(client, cache);

    assertEquals("Cache must contain existing tenant",
        existingTenant, cache.getByClassWithName(Tenant.class, TEN_NAME));
  }

  @Test
  public void whenInstallingArtifact() throws IOException {
    BaseIAArtifact tenantArtifact = new TenantIaHandler(TEN_NAME);
    Tenants tenants = mock(Tenants.class);
    Tenant resultTenant = new Tenant();
    resultTenant.setName(TEN_NAME);
    when(client.follow(any(), any(), eq(Tenants.class))).thenReturn(tenants);
    when(tenants.byName(TEN_NAME)).thenReturn(null);
    when(client.createCollectionItem(any(), any(), eq(LINK_ADD), eq(LINK_SELF))).thenReturn(resultTenant);

    tenantArtifact.install(client, cache);

    assertEquals("Cache must contain created tenant",
        resultTenant, cache.getByClassWithName(Tenant.class, TEN_NAME));
  }

  @Test
  public void whenInstallingDefaultTenant() throws IOException {
    BaseIAArtifact tenantArtifact = new TenantIaHandler();
    Tenant defaultTenant = new Tenant();
    defaultTenant.setName("INFOARCHIVE");
    when(client.follow(any(), eq(LINK_TENANT), eq(Tenant.class))).thenReturn(defaultTenant);

    tenantArtifact.install(client, cache);

    assertEquals("Cache must contain default tenant",
        defaultTenant, cache.getByClassWithName(Tenant.class, "INFOARCHIVE"));
  }

}
