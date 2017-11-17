/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build a tenant.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <C> The type of configuration to build
 */
public class TenantBuilder<C> extends NamedObjectBuilder<ConfigurationBuilder<C>, TenantBuilder<C>, C> {

  protected TenantBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "tenant");
    named("INFOARCHIVE");
  }

  /**
   * Start building an application for this tenant.
   * @return This builder
   */
  public ApplicationBuilder<C> withApplication() {
    return new ApplicationBuilder<>(this);
  }

}
