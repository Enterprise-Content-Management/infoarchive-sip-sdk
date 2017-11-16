/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class TenantBuilder extends NamedObjectBuilder<ConfigurationBuilder, TenantBuilder> {

  protected TenantBuilder(ConfigurationBuilder parent) {
    super(parent, "tenants");
    named("INFOARCHIVE");
  }

  public ApplicationBuilder withApplication() {
    return new ApplicationBuilder(this);
  }

}
