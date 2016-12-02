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
import java.util.Objects;

public final class TenantIaHandler extends BaseIAArtifact {

  public static TenantExtractor extractor() {
    return new TenantExtractor();
  }

  private final String tenantName;

  public TenantIaHandler(String tenantName) {
    this.tenantName = tenantName;
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

  private static final class TenantExtractor extends ArtifactExtractor {
    @Override
    public BaseIAArtifact extract(Object representation) {
      return new TenantIaHandler(asString(representation));
    }

    @Override
    public String getFieldName() {
      return "tenant";
    }
  }
}
