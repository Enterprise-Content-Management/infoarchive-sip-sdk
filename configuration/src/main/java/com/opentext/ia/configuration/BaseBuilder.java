/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.UUID;

import org.atteo.evo.inflector.English;


/**
 * Base class for all configuration builders.
 * @author Ray Sinnema
 * @since 9.4.0
 *
 * @param <P> The type of parent builder
 * @param <C> The type of configuration to build
 */
abstract class BaseBuilder<P extends BaseBuilder<?, C>, C> {

  private final ConfigurationObject object;
  private final ConfigurationProducer<C> producer;
  private final P parent;

  protected BaseBuilder(P parent, String type) {
    this.producer = null;
    this.parent = parent;
    this.object = new ConfigurationObject(type);
  }

  protected BaseBuilder(ConfigurationProducer<C> producer, String type) {
    this.producer = producer;
    this.parent = null;
    this.object = new ConfigurationObject(type);
  }

  protected String someName() {
    return UUID.randomUUID().toString();
  }

  protected void setProperty(String name, Object value) {
    object.setProperty(name, value);
  }

  /**
   * End this builder.
   * @return The parent builder
   */
  public P end() {
    parent.addChildObject(English.plural(object.getType()), object);
    return parent;
  }

  protected void addChildObject(String collection, ConfigurationObject childObject) {
    if (object.getType() != null) {
      childObject.setProperty(object.getType(), object.getProperties().optString("name"));
    }
    object.addChildObject(collection, childObject);
  }

  /**
   * Build the configuration.
   * @return The configuration that was built
   */
  public Configuration<C> build() {
    if (parent == null) {
      return producer.produce(object);
    }
    return end().build();
  }

  @Override
  public String toString() {
    return object.toString();
  }

}
