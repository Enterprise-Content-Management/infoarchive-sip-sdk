/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build a file system root.
 * @author Ray Sinnema
 * @since 9.5.0
 *
 * @param <C> The type of configuration to build
 */
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

  /**
   * Set the type to Isilon.
   * @return This builder
   */
  public FileSystemRootBuilder<C> onIsilon() {
    setType("ISILON");
    return this;
  }

  private void setPath(String path) {
    setProperty("path", path);
  }

  /**
   * Set the path.
   * @param path the path to set
   * @return This builder
   */
  public FileSystemRootBuilder<C> at(String path) {
    setPath(path);
    return this;
  }

  /**
   * Set the description.
   * @param description the description to set
   * @return This builder
   */
  public FileSystemRootBuilder<C> withDescription(String description) {
    setProperty("description", description);
    return this;
  }

}
