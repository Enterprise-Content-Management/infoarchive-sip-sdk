/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


public class WhenPrintingXmlDocuments extends XmlBuilderTestCase<Void> {

  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @Override
  protected XmlBuilder<Void> newBuilder() {
    return XmlBuilder.newDocument(new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8)));
  }

  @Override
  protected String getOutput() {
    getBuilder().build();
    try {
      return output.toString(StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
  }

}
