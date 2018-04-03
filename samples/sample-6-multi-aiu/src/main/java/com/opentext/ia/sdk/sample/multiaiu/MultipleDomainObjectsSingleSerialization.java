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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.HashedContents;
import com.opentext.ia.sdk.sip.PdiAssembler;
import com.opentext.ia.sdk.sip.XmlPdiAssembler;
import com.opentext.ia.sdk.support.io.DataBuffer;
import com.opentext.ia.sdk.support.io.MemoryBuffer;


public class MultipleDomainObjectsSingleSerialization {

  private static final URI NAMESPACE = URI.create("urn:opentext:ia:schema:sample:multiple:1.0");

  @SuppressWarnings("PMD.AvoidPrintStackTrace")
  public static void main(String[] args) {
    try {
      new MultipleDomainObjectsSingleSerialization().run();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("PMD.SystemPrintln")
  private void run() throws IOException {
    // Assembler for PDI, where the domain object is flexible
    PdiAssembler<Object> pdiAssembler = new XmlPdiAssembler<Object>(NAMESPACE, "aiu") {
      private final ObjectMapper mapper = new XmlMapper();

      @Override
      protected void doAdd(Object domainObject, Map<String, ContentInfo> ignored) {
        try {
          getBuilder().xml(mapper.writeValueAsString(domainObject));
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }
    };

    // Collect the PDI XML in an in-memory buffer. For large PDIs, use a FileBuffer instead.
    DataBuffer dataBuffer = new MemoryBuffer();
    pdiAssembler.start(dataBuffer);
    try {
      // Assemble the PDI by adding domain objects to it.
      // In this sample, the domain objects are flexible objects
      pdiAssembler.add(wrapDomainObject(new Animal("Gnu")));
      pdiAssembler.add(wrapDomainObject(new Person("Remon", "Sinnema")));
    } finally {
      pdiAssembler.end();
    }
    try (InputStream pdi = dataBuffer.openForReading()) {
      System.out.println(IOUtils.toString(pdi, StandardCharsets.UTF_8));
    }
  }

  private HashedContents<Object> wrapDomainObject(Object aiu) {
    // The HashedContents class enriches the domain object with information about content objects. We don't need that
    // in this simple sample, so we just pass in an empty map.
    return new HashedContents<>(aiu, Collections.emptyMap());
  }

}
