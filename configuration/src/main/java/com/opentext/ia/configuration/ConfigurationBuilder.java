/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class ConfigurationBuilder extends BaseBuilder<BaseBuilder<?>> {

  public ConfigurationBuilder(ConfigurationWriter manager) {
    super(manager);
  }

  public TenantBuilder withTenant() {
    return new TenantBuilder(this);
  }

  public ApplicationBuilder withApplication() {
    return withTenant().withApplication();
  }

  public SearchBuilder withSearch() {
    return withApplication().withSearch();
  }

}
