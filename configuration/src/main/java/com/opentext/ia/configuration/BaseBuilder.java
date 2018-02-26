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
public abstract class BaseBuilder<P extends BaseBuilder<?, C>, C extends Configuration<?>> {

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
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  protected Object getProperty(String name) {
    return object.getProperties().get(name);
  }

  protected void setProperty(String name, Object value) {
    object.setProperty(name, value);
  }

  protected void setUninitialized(String... properties) {
    for (String property : properties) {
      setProperty(property, null);
    }
  }

  protected boolean hasProperty(String name) {
    return object.hasProperty(name);
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
    if (parentProperty() != null) {
      childObject.setProperty(parentProperty(), object.getProperties().optString("name"));
    }
    object.addChildObject(collection, childObject);
  }

  protected String parentProperty() {
    return object.getType();
  }

  /**
   * Build the configuration.
   * @return The configuration that was built
   */
  public C build() {
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
