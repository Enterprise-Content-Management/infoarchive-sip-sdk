/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build InfoArchive configurations.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <C> The type of configuration to build
 */
public class ConfigurationBuilder<C> extends BaseBuilder<BaseBuilder<?, C>, C> {

  public ConfigurationBuilder(ConfigurationProducer<C> producer) {
    super(producer, null);
  }

  /**
   * Start building a tenant.
   * @return A builder for the new tenant
   */
  public TenantBuilder<C> withTenant() {
    return new TenantBuilder<>(this);
  }

  /**
   * Start building an application in a new tenant.
   * @return A builder for the new application
   */
  public ApplicationBuilder<C> withApplication() {
    return withTenant().withApplication();
  }

  /**
   * Start building a search in a new application in a new tenant.
   * @return A builder for the new search
   */
  public SearchBuilder<C> withSearch() {
    return withApplication().withSearch();
  }

}
