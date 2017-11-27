/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class PdiSchemaBuilder<C extends Configuration<?>>
    extends NamedObjectBuilder<ApplicationBuilder<C>, PdiSchemaBuilder<C>, C> {

  protected PdiSchemaBuilder(ApplicationBuilder<C> parent) {
    super(parent, "pdiSchema");
    setFormat("xsd");
  }

  private void setFormat(String format) {
    setProperty("format", format);
  }

  public PdiSchemaBuilder<C> ofType(String format) {
    setFormat(format);
    return this;
  }

  public PdiSchemaBuilder<C> as(String content) {
    setProperty("content", content);
    return this;
  }

}
