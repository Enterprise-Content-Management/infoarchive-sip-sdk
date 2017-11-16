/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.UUID;

import org.atteo.evo.inflector.English;


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

  public P end() {
    parent.addChildObject(English.plural(object.getType()), object);
    return parent;
  }

  protected void addChildObject(String collection, ConfigurationObject childObject) {
    object.addChildObject(collection, childObject);
  }

  public Configuration<C> build() {
    if (parent == null) {
      return producer.produce(object);
    }
    return end().build();
  }

}
