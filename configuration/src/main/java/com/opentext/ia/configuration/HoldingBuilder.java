/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

/**
 * Build a holding.
 * 
 * @author Ray Sinnema
 * @since 9.5.0
 *
 * @param <C> The type of configuration to build
 */
public class HoldingBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ApplicationBuilder<C>, HoldingBuilder<C>, C> {

  protected HoldingBuilder(ApplicationBuilder<C> parent) {
    super(parent, "holding");
    setLibraryMode("PRIVATE");
  }

  private void setLibraryMode(String libraryMode) {
    setProperty("libraryMode", libraryMode);
  }

  public HoldingBuilder<C> inPool() {
    setLibraryMode("POOLED");
    return this;
  }

  public HoldingBuilder<C> inAggregate() {
    setLibraryMode("AGGREGATE");
    return this;
  }

}
