/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class SearchBuilder<C> extends NamedObjectBuilder<ApplicationBuilder<C>, SearchBuilder<C>, C> {

  protected SearchBuilder(ApplicationBuilder<C> parent, String type) {
    super(parent, type);
  }

}
