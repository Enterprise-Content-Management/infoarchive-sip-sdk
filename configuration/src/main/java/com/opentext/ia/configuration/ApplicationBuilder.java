/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class ApplicationBuilder<C> extends NamedObjectBuilder<TenantBuilder<C>, ApplicationBuilder<C>, C> {

  public ApplicationBuilder(TenantBuilder<C> parent) {
    super(parent, "application");
  }

  public SearchBuilder<C> withSearch() {
    return new SearchBuilder<>(this, "search");
  }

}
