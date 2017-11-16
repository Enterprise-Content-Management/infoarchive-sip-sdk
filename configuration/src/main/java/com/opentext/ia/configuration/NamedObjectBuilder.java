/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class NamedObjectBuilder<P extends BaseBuilder<?, C>, S extends NamedObjectBuilder<?, ?, C>, C>
    extends BaseBuilder<P, C> {

  protected NamedObjectBuilder(P parent, String type) {
    super(parent, type);
    named(someName());
  }

  @SuppressWarnings("unchecked")
  public final S named(String name) {
    setProperty("name", name);
    return (S)this;
  }

}
