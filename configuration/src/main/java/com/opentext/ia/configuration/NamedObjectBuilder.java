/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Builder for named configuration objects.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <P> The type of the parent builder
 * @param <S> The type of this builder
 * @param <C> The type of configuration to build
 */
public class NamedObjectBuilder<P extends BaseBuilder<?, C>, S extends NamedObjectBuilder<?, ?, C>, C extends Configuration<?>>
    extends BaseBuilder<P, C> {

  protected NamedObjectBuilder(P parent, String type) {
    super(parent, type);
    named(typePrefix(type) + someName());
  }

  private String typePrefix(String type) {
    return type.substring(0, Math.min(type.length(), 3));
  }

  /**
   * Set the name of the object.
   * @param name The name of the object
   * @return This builder
   */
  @SuppressWarnings("unchecked")
  public final S named(String name) {
    setProperty("name", name);
    return (S)this;
  }

}
