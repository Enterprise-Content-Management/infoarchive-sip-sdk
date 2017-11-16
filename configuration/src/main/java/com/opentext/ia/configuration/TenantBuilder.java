/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class TenantBuilder<C> extends NamedObjectBuilder<ConfigurationBuilder<C>, TenantBuilder<C>, C> {

  protected TenantBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "tenant");
    named("INFOARCHIVE");
  }

  public ApplicationBuilder<C> withApplication() {
    return new ApplicationBuilder<>(this);
  }

}
