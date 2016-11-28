package com.emc.ia.sdk.configuration.artifacts;


import com.emc.ia.sdk.configuration.ArtifactExtractor;
import com.emc.ia.sdk.configuration.BaseIAArtifact;
import com.emc.ia.sdk.configuration.Extractor;
import com.emc.ia.sdk.configuration.IACache;
import com.emc.ia.sdk.sip.client.dto.Services;
import com.emc.ia.sdk.sip.client.dto.Tenant;
import com.emc.ia.sdk.sip.client.dto.Tenants;
import com.emc.ia.sdk.support.rest.RestClient;

import java.io.IOException;

public final class TenantIaHandler extends BaseIAArtifact {

  public static Extractor extractor() {
    return new TenantExtractor();
  }

  private final Tenant tenant;

  public TenantIaHandler(Tenant source) {
    this.tenant = source;
  }

  @Override
  protected void installArtifact(RestClient client, IACache cache) throws IOException {
    Tenants tenants = client.follow(cache.getFirst(Services.class), LINK_TENANTS, Tenants.class);
    Tenant createdTenant = tenants.byName(tenant.getName());
    if (createdTenant == null) {
      createdTenant = client.createCollectionItem(tenants, tenant, LINK_ADD, LINK_SELF);
    }
    cache.cacheOne(createdTenant);
  }

  private static final class TenantExtractor extends ArtifactExtractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      String tenantName = asString(representation);
      Tenant tenant = new Tenant();
      tenant.setName(tenantName);
      return new TenantIaHandler(tenant);
    }

    @Override
    public String getFieldName() {
      return "tenant";
    }
  }
}
