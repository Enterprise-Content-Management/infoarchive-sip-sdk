/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.configuration;


public class PdiSchemaBuilder<C extends Configuration<?>>
    extends ContentObjectBuilder<ApplicationBuilder<C>, PdiSchemaBuilder<C>, C> {

  protected PdiSchemaBuilder(ApplicationBuilder<C> parent) {
    super(parent, "pdiSchema");
  }

  public ContentBuilder<PdiSchemaBuilder<C>> withContent() {
    return withContent("xsd");
  }

  @Override
  public void addContent(String format, String text) {
    setProperty("format", format);
    setProperty("content", text);
  }

}
