/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

public class SpaceRootRdbDatabaseBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<SpaceBuilder<C>, SpaceRootRdbDatabaseBuilder<C>, C> {

  protected SpaceRootRdbDatabaseBuilder(SpaceBuilder<C> parent) {
    super(parent, "spaceRootRdbDatabase");
  }

  @Override
  protected String parentProperty() {
    return "parentSpaceRootRdbDatabase";
  }

}
