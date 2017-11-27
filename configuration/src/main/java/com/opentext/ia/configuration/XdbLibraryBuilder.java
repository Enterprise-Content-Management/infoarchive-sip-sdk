/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

public class XdbLibraryBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<SpaceRootXdbLibraryBuilder<C>, XdbLibraryBuilder<C>, C> {

  protected XdbLibraryBuilder(SpaceRootXdbLibraryBuilder<C> parent) {
    super(parent, "xdbLibrary");
    setType("DATA");
    setXdbMode("PRIVATE");
  }

  private void setType(String type) {
    setProperty("type", type);
  }

  public XdbLibraryBuilder<C> storingSearchResult() {
    setType("SEARCH_RESULT");
    return this;
  }

  public XdbLibraryBuilder<C> storingSearchResults() {
    setType("SEARCH_RESULTS");
    return this;
  }

  public XdbLibraryBuilder<C> storingManagedItems() {
    setType("MANAGED_ITEMS");
    return this;
  }

  private void setXdbMode(String xdbMode) {
    setProperty("xdbMode", xdbMode);
  }

  public XdbLibraryBuilder<C> inAggregate() {
    setXdbMode("AGGREGATE");
    return this;
  }

  public XdbLibraryBuilder<C> inPool() {
    setXdbMode("POOLED");
    return this;
  }

  public XdbLibraryBuilder<C> at(String subPath) {
    setProperty("subPath", subPath);
    return this;
  }

}
