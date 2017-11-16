/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class ApplicationBuilder extends NamedObjectBuilder<TenantBuilder, ApplicationBuilder> {

  public ApplicationBuilder(TenantBuilder parent) {
    super(parent, "applications");
  }

  public SearchBuilder withSearch() {
    return new SearchBuilder(this, "searches");
  }

}
