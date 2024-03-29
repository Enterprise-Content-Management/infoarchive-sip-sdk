/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.XMLConstants;

import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.io.MemoryBuffer;
import com.opentext.ia.sdk.support.xml.XmlBuilder;
import com.opentext.ia.sdk.support.xml.XmlUtil;
import com.opentext.ia.test.TestCase;


class WhenAssemblingXmlPdis extends TestCase {

  @Test
  void shouldValidateWhenSchemaIsProvided() throws IOException {
    assertThrows(IOException.class, () -> assemblePdi(testSchema()));
  }

  private void assemblePdi(InputStream schema) throws IOException {
    Assembler<HashedContents<String>> pdiAssembler = new TestPdiAssembler(schema, randomString());
    pdiAssembler.start(new MemoryBuffer());
    pdiAssembler.end();
  }

  private InputStream testSchema() {
    return new ByteArrayInputStream(XmlUtil.toString(XmlBuilder.newDocument()
        .namespace(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        .element("schema")
            .element("element")
                .attribute("name", randomString())
                .attribute("type", "string")
            .end()
        .end()
      .build())
      .getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void shouldNotValidateWhenNoSchemaIsProvided() throws IOException {
    assemblePdi(null);
  }


  private static class TestPdiAssembler extends XmlPdiAssembler<String> {

    TestPdiAssembler(InputStream schema, String domainObjectName) {
      super(URI.create("http://mycompany.com/ns/example"), domainObjectName, schema);
    }

    @Override
    protected void doAdd(String domainObject, Map<String, ContentInfo> contentInfo) {
      // Nothing to do
    }

  }

}
