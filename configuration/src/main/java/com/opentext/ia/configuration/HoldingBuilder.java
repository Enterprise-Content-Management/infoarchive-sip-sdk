/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class HoldingBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ApplicationBuilder<C>, HoldingBuilder<C>, C> {

  protected HoldingBuilder(ApplicationBuilder<C> parent) {
    super(parent, "holding");
  }

}
