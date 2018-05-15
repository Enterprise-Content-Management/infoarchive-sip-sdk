/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.hello;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.atteo.evo.inflector.English;

import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.FileGenerator;
import com.opentext.ia.sdk.sip.PackagingInformation;
import com.opentext.ia.sdk.sip.PdiAssembler;
import com.opentext.ia.sdk.sip.SipAssembler;
import com.opentext.ia.sdk.sip.SipMetrics;
import com.opentext.ia.sdk.sip.XmlPdiAssembler;


@SuppressWarnings("PMD")
public class HelloSip {

  public static void main(String[] args) {
    try {
      new HelloSip().run();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void run() throws IOException {
    System.out.println("\nSample 1: Creating our first SIP");
    // Tell InfoArchive where and how to archive the data
    URI entityUri = URI.create("urn:com.opentext.ia.sdk.sample.greeting:1.0");
    String entityName = "greeting";
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
            .application("greetingApplication")
            .holding("greetingHolding")
            .producer("world")
            .entity(entityName)
            .schema(entityUri.toString())
        .end()
    .build();

    // Define a mapping from our domain object to the PDI XML
    PdiAssembler<Greeting> pdiAssembler = new XmlPdiAssembler<Greeting>(entityUri, entityName) {
      @Override
      protected void doAdd(Greeting greeting, Map<String, ContentInfo> ignored) {
        getBuilder().element("message", greeting.getMessage());
      }
    };

    // Assemble the SIP
    SipAssembler<Greeting> sipAssembler = SipAssembler.forPdi(prototype, pdiAssembler);
    FileGenerator<Greeting> generator = new FileGenerator<>(sipAssembler, () -> new File("hello-sip.zip"));
    Greeting greeting = new Greeting("Hello, SIP");
    SipMetrics metrics = (SipMetrics)generator.generate(greeting).getMetrics();
    System.out.printf("  Added %d %s to SIP of %d bytes in %d ms%n", metrics.numAius(),
        English.plural("AIU", (int)metrics.numAius()), metrics.sipFileSize(), metrics.assemblyTime());
  }

}
