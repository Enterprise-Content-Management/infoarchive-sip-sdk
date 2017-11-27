/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class SpaceBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ApplicationBuilder<C>, SpaceBuilder<C>, C> {

  protected SpaceBuilder(ApplicationBuilder<C> parent) {
    super(parent, "space");
  }

  public SpaceRootXdbLibraryBuilder<C> withSpaceRootXdbLibrary() {
    return new SpaceRootXdbLibraryBuilder<>(this);
  }

}
