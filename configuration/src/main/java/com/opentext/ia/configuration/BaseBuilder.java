/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

import java.util.UUID;

import org.json.JSONObject;


abstract class BaseBuilder<P extends BaseBuilder<?>> {

  private final JSONObject object = new JSONObject();
  private final ConfigurationWriter manager;
  private final P parent;
  private String collection;

  protected BaseBuilder(P parent, String collection) {
    this.manager = null;
    this.parent = parent;
    this.collection = collection;
  }

  protected BaseBuilder(ConfigurationWriter manager) {
    this.manager = manager;
    this.parent = null;
  }

  protected String someName() {
    return UUID.randomUUID().toString();
  }

  protected void setField(String name, Object value) {
    object.put(name, value);
  }

  public P end() {
    if (parent == null) {
      throw new IllegalStateException("Cannot end top-level builder; use build() instead");
    }
    parent.addSubObject(collection, object);
    return parent;
  }

  protected void addSubObject(String key, JSONObject subObject) {
  }

  public void build() {
    if (manager == null) {
      throw new IllegalStateException("Use build intermediate builder; use end() instead");
    }
    manager.build(object);
  }

}
