/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class XdbLibraryBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<SpaceRootXdbLibraryBuilder<C>, XdbLibraryBuilder<C>, C> {

  protected XdbLibraryBuilder(SpaceRootXdbLibraryBuilder<C> parent) {
    super(parent, "xdbLibrary");
  }

}
