/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class SpaceRootFolderBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<SpaceBuilder<C>, SpaceRootFolderBuilder<C>, C> {

  protected SpaceRootFolderBuilder(SpaceBuilder<C> parent, String fileSystemRootName) {
    super(parent, "spaceRootFolder");
    setProperty("fileSystemRoot", fileSystemRootName);
  }

}
