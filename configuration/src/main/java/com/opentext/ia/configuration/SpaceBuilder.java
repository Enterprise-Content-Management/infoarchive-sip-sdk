/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;

/**
 * Build a space.
 * @author Ray Sinnema
 * @since 9.5.0
 *
 * @param <C> The type of configuration to build
 */
public class SpaceBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ApplicationBuilder<C>, SpaceBuilder<C>, C> {

  protected SpaceBuilder(ApplicationBuilder<C> parent) {
    super(parent, "space");
  }

  /**
   * Start building a space root xDB library.
   * @return A new builder
   */
  public SpaceRootXdbLibraryBuilder<C> withSpaceRootXdbLibrary() {
    return new SpaceRootXdbLibraryBuilder<>(this);
  }

  /**
   * Start building a space root RDB Database.
   * @return A new builder
   */
  public SpaceRootRdbDatabaseBuilder<C> withSpaceRootRdbDatabase() {
    return new SpaceRootRdbDatabaseBuilder<>(this);
  }

  /**
   * Start building a space root folder.
   * @param fileSystemRootName the name of the file system root in which information in this space
   *          root folder is stored
   * @return A new builder
   */
  public SpaceRootFolderBuilder<C> withSpaceRootFolder(String fileSystemRootName) {
    return new SpaceRootFolderBuilder<>(this, fileSystemRootName);
  }

}
