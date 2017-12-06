/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


/**
 * Build a holding.
 * @author Ray Sinnema
 * @since 9.5.0
 *
 * @param <C> The type of configuration to build
 */
public class HoldingBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ApplicationBuilder<C>, HoldingBuilder<C>, C> {

  protected HoldingBuilder(ApplicationBuilder<C> parent) {
    super(parent, "holding");
  }

}
