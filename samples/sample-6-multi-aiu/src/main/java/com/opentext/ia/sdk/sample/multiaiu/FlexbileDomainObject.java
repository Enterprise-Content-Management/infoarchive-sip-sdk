/*
 * Copyright (c) 2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.multiaiu;

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


public class FlexbileDomainObject {

  private static final URI NAMESPACE = URI.create("urn:opentext:ia:schema:sample:flexible:1.0");

  @SuppressWarnings("PMD.AvoidPrintStackTrace")
  public static void main(String[] args) {
    try {
      new FlexbileDomainObject().run();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("PMD.SystemPrintln")
  private void run() throws IOException {
    // Assembler for PDI, where the domain object is flexible
    PdiAssembler<Map<String, String>> pdiAssembler = new XmlPdiAssembler<Map<String, String>>(NAMESPACE, "aiu") {
      @Override
      protected void doAdd(Map<String, String> aiu, Map<String, ContentInfo> ignored) {
        aiu.forEach((key, value) -> getBuilder().element(key, value));
      }
    };

    // Collect the PDI XML in an in-memory buffer. For large PDIs, use a FileBuffer instead.
    DataBuffer dataBuffer = new MemoryBuffer();
    pdiAssembler.start(dataBuffer);
    try {
      // Assemble the PDI by adding domain objects to it.
      // In this sample, the domain objects are flexible objects
      pdiAssembler.add(wrapDomainObject("foo", "bar"));
      pdiAssembler.add(wrapDomainObject("gnu", "gnat"));
    } finally {
      pdiAssembler.end();
    }
    try (InputStream pdi = dataBuffer.openForReading()) {
      System.out.println(IOUtils.toString(pdi, StandardCharsets.UTF_8));
    }
  }

  private HashedContents<Map<String, String>> wrapDomainObject(String key, String value) {
    // The HashedContents class enriches the domain object with information about content objects. We don't need that
    // in this simple sample, so we just pass in an empty map.
    return new HashedContents<>(Collections.singletonMap(key, value), Collections.emptyMap());
  }

}
