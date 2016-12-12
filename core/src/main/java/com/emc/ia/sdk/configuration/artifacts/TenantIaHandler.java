/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.configuration.artifacts;

import java.io.IOException;
import java.util.Objects;

import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.sip.client.dto.Tenants;
import com.emc.ia.sdk.support.rest.RestClient;

public final class TenantIaHandler extends BaseIAArtifact {

  public static TenantExtractor extractor() {
    return new TenantExtractor();
  }

  private final String tenantName;

  public TenantIaHandler() {
    this.tenantName = null;
  }

  public TenantIaHandler(String tenantName) {
    this.tenantName = Objects.requireNonNull(tenantName);
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Tenant usedTenant;
    if (tenantName == null) {
      usedTenant = client.follow(cache.getFirst(Services.class), LINK_TENANT, Tenant.class);
    } else {
      Tenants tenants = client.follow(cache.getFirst(Services.class), LINK_TENANTS, Tenants.class);
      usedTenant = tenants.byName(tenantName);
      if (usedTenant == null) {
        usedTenant = new Tenant();
        usedTenant.setName(tenantName);
        usedTenant = client.createCollectionItem(tenants, usedTenant, LINK_ADD, LINK_SELF);
      }
    }
    cache.cacheOne(usedTenant);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof TenantIaHandler)) {
      return false;
    }
    TenantIaHandler handler = (TenantIaHandler)other;
    return Objects.equals(tenantName, handler.tenantName);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(tenantName);
  }

  private static final class TenantExtractor extends ArtifactExtractor {
    @Override
    public TenantIaHandler extract(Object representation) {
      return new TenantIaHandler(asString(representation));
    }

    @Override
    public String getFieldName() {
      return "tenant";
    }
  }
}
