/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;

import com.opentext.ia.sdk.support.io.RepeatableInputStream;
import com.opentext.ia.sdk.support.test.TestCase;
import com.opentext.ia.sdk.support.xml.XmlUtil;

public class XmlTestCase extends TestCase {

  protected Document assertValidXmlFile(File file, String documentType, String schema) throws IOException {
    try (InputStream stream = new FileInputStream(file)) {
      return assertValidXml(stream, documentType, schema);
    }
  }

  protected Document assertValidXml(InputStream stream, String humanFriendlyDocumentType, String schema)
      throws IOException {
    RepeatableInputStream repetableStream = new RepeatableInputStream(stream);
    XmlUtil.validate(repetableStream.get(), getClass().getResourceAsStream("/" + schema), humanFriendlyDocumentType);
    return XmlUtil.parse(repetableStream.get());
  }

}
