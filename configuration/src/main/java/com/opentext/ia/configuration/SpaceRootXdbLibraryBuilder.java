/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class SpaceRootXdbLibraryBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<SpaceBuilder<C>, SpaceRootXdbLibraryBuilder<C>, C> {

  protected SpaceRootXdbLibraryBuilder(SpaceBuilder<C> parent) {
    super(parent, "spaceRootXdbLibrary");
  }

  public XdbLibraryBuilder<C> withXdbLibrary() {
    return new XdbLibraryBuilder<>(this);
  }

  @Override
  protected String parentProperty() {
    return "parentSpaceRootXdbLibrary";
  }

}
