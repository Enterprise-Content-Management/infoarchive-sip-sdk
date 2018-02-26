/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class PdiBuilder<C extends Configuration<?>>
    extends ContentObjectBuilder<ApplicationBuilder<C>, PdiBuilder<C>, C> {

  protected PdiBuilder(ApplicationBuilder<C> parent) {
    super(parent, "pdi");
  }

  public ContentBuilder<PdiBuilder<C>> withContent() {
    return withContent("xml");
  }

}
