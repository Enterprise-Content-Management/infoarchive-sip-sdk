/*
 * Copyright (c) 2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.pdi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.HashedContents;
import com.opentext.ia.sdk.sip.PdiAssembler;
import com.opentext.ia.sdk.sip.XmlPdiAssembler;
import com.opentext.ia.sdk.support.io.DataBuffer;
import com.opentext.ia.sdk.support.io.MemoryBuffer;


/**
 * Sample program that shows how to assemble just the PDI XML from some data.
 */
@SuppressWarnings("PMD")
public class AssemblePdi {

  private static final URI NAMESPACE = URI.create("urn:opentext:ia:schema:sample:text:1.0");

  public static void main(String[] args) {
    try {
      new AssemblePdi().run();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void run() throws IOException {
    // Assembler for PDI, where the domain object is of type String
    PdiAssembler<String> pdiAssembler = new XmlPdiAssembler<String>(NAMESPACE, "message") {
      @Override
      protected void doAdd(String domainObject, Map<String, ContentInfo> ignored) {
        getBuilder().element("text", domainObject);
      }
    };

    // Collect the PDI XML in an in-memory buffer. For large PDIs, use a FileBuffer instead.
    DataBuffer dataBuffer = new MemoryBuffer();
    pdiAssembler.start(dataBuffer);
    try {
      // Assemble the PDI by adding domain objects to it.
      // In this sample, the domain objects are the strings "foo" and "bar".
      pdiAssembler.add(wrapDomainObject("foo"));
      pdiAssembler.add(wrapDomainObject("bar"));
    } finally {
      pdiAssembler.end();
    }
    try (InputStream pdi = dataBuffer.openForReading()) {
      System.out.println(IOUtils.toString(pdi, StandardCharsets.UTF_8));
    }
  }

  private HashedContents<String> wrapDomainObject(String domainObject) {
    // The HashedContents class enriches the domain object with information about content objects. We don't need that
    // in this simple sample, so we just pass in an empty map.
    return new HashedContents<>(domainObject, Collections.emptyMap());
  }

}
