/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.xml;

import org.w3c.dom.Document;


public class WhenBuildingXmlDocuments extends XmlBuilderTestCase<Document> {

  @Override
  protected XmlBuilder<Document> newBuilder() {
    return XmlBuilder.newDocument();
  }

  @Override
  protected String getOutput() {
    return XmlUtil.toString(getBuilder().build().getDocumentElement());
  }

}
