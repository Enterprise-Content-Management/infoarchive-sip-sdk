/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

public class SpaceRootRdbDatabaseBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<SpaceBuilder<C>, SpaceRootRdbDatabaseBuilder<C>, C> {

  protected SpaceRootRdbDatabaseBuilder(SpaceBuilder<C> parent) {
    super(parent, "spaceRootRdbDatabase");
  }

  private void setName(String name) {
    setProperty("name", name);
  }

  public SpaceRootRdbDatabaseBuilder<C> withName(String name) {
    setName(name);
    return this;
  }

  public RdbDatabaseBuilder<SpaceRootRdbDatabaseBuilder<C>, C> withRdbDatabase() {
    return new RdbDatabaseBuilder<>(this);
  }

  @Override
  protected String parentProperty() {
    return "parentSpaceRootRdbDatabase";
  }

}
