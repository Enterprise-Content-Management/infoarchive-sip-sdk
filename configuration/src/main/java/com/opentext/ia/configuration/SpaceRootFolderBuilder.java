/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build a space root folder.
 * @author Ray Sinnema
 * @since 9.5.0
 *
 * @param <C> The type of configuration to build
 */
public class SpaceRootFolderBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<SpaceBuilder<C>, SpaceRootFolderBuilder<C>, C> {

  protected SpaceRootFolderBuilder(SpaceBuilder<C> parent, String fileSystemRootName) {
    super(parent, "spaceRootFolder");
    setProperty("fileSystemRoot", fileSystemRootName);
  }

}
