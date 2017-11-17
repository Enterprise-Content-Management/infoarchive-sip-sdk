/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build a search.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <C> The type of configuration to build
 */
public class SearchBuilder<C> extends NamedObjectBuilder<ApplicationBuilder<C>, SearchBuilder<C>, C> {

  protected SearchBuilder(ApplicationBuilder<C> parent, String type) {
    super(parent, type);
    setState("DRAFT");
    setUninitialized("description");
  }

  private void setState(String state) {
    setProperty("state", state);
  }

  public SearchBuilder<C> published() {
    setState("PUBLISHED");
    return this;
  }

  public SearchBuilder<C> withDescription(String description) {
    setProperty("description", description);
    return this;
  }

}
