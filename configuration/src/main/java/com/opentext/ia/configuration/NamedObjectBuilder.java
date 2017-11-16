/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class NamedObjectBuilder<P extends BaseBuilder<?>, S extends BaseBuilder<?>> extends BaseBuilder<P> {

  protected NamedObjectBuilder(P parent, String collection) {
    super(parent, collection);
    named(someName());
  }

  @SuppressWarnings("unchecked")
  public S named(String name) {
    setField("name", name);
    return (S)this;
  }

}
