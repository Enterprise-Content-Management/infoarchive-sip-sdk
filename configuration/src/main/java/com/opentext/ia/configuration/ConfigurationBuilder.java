/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class ConfigurationBuilder<C> extends BaseBuilder<BaseBuilder<?, C>, C> {

  public ConfigurationBuilder(ConfigurationProducer<C> producer) {
    super(producer, null);
  }

  public TenantBuilder<C> withTenant() {
    return new TenantBuilder<>(this);
  }

  public ApplicationBuilder<C> withApplication() {
    return withTenant().withApplication();
  }

  public SearchBuilder<C> withSearch() {
    return withApplication().withSearch();
  }

}
