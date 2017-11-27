/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class FileSystemRootBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ConfigurationBuilder<C>, FileSystemRootBuilder<C>, C> {

  protected FileSystemRootBuilder(ConfigurationBuilder<C> parent) {
    super(parent, "fileSystemRoot");
    setPath("/data/root");
    setType("FILESYSTEM");
    setUninitialized("description");
  }

  private void setType(String type) {
    setProperty("type", type);
  }

  public FileSystemRootBuilder<C> onIsilon() {
    setType("ISILON");
    return this;
  }

  private void setPath(String path) {
    setProperty("path", path);
  }

  public FileSystemRootBuilder<C> at(String path) {
    setPath(path);
    return this;
  }

  public FileSystemRootBuilder<C> withDescription(String description) {
    setProperty("description", description);
    return this;
  }

}
