/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.ingest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

import com.opentext.ia.sdk.client.ArchiveClient;
import com.opentext.ia.sdk.server.configuration.ArchiveClients;
import com.opentext.ia.sdk.server.configuration.YamlBasedConfigurer;
import com.opentext.ia.sdk.server.configuration.YamlConfiguration;
import com.opentext.ia.sdk.sip.*;
import com.opentext.ia.sdk.support.io.FileSupplier;


public class YamlSipIngester {
  private static final String SAMPLE_HOLDING = "Animals";
  private static final String SAMPLE_SCHEMA = "pdi-schema.xsd";
  private static final String SAMPLE_NAMESPACE = "urn:opentext:ia:schema:sample:animal:1.0";
  private static final String SAMPLE_FILES_PATH = "src/main/resources";

  @SuppressWarnings("PMD.AvoidPrintStackTrace")
  public static void main(String[] args) {
    try {
      String rootPath = new File(".").getCanonicalPath();
      new YamlSipIngester().run(rootPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("PMD.SystemPrintln")
  private void run(String rootPath) throws IOException {
    // Tell InfoArchive where and how to archive the data
    URI entityUri = URI.create(SAMPLE_NAMESPACE);
    String entityName = "animal";
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
        .application(SAMPLE_HOLDING)
        .holding(SAMPLE_HOLDING)
        .producer("SIP-SDK")
        .entity(entityName)
        .schema(entityUri.toString())
        .end()
        .build();

    // Define a mapping from our domain object to the PDI XML
    XmlPdiAssembler<File> pdiAssembler;
    try (InputStream schema = getClass().getResourceAsStream('/' + SAMPLE_SCHEMA)) {
      pdiAssembler = new XmlPdiAssembler<File>(entityUri, entityName, schema) {
        @Override
        protected void doAdd(File value, Map<String, ContentInfo> ignored) {
          getBuilder()
              .element("animal_name", value.getName().substring(0, value.getName().lastIndexOf(".")))
              .element("file_path", relativePath(value, rootPath));
        }
      };
    }

    DigitalObjectsExtraction<File> contentAssembler = file -> Collections.singleton(
        DigitalObject.fromFile(relativePath(file, rootPath), file)
    ).iterator();

    // Assemble the SIP
    SipAssembler<File> sipAssembler = SipAssembler.forPdiAndContent(prototype, pdiAssembler, contentAssembler);
    FileGenerator<File> generator = new FileGenerator<>(sipAssembler, FileSupplier.fromTemporaryDirectory());

    File f1 = new File(SAMPLE_FILES_PATH, "ape.dat");
    File f2 = new File(SAMPLE_FILES_PATH, "bear.dat");
    File f3 = new File(SAMPLE_FILES_PATH, "cobra.dat");

    FileGenerationMetrics metrics = generator.generate(Arrays.asList(f1, f2, f3));
    File assembledSip = metrics.getFile();

    // Get an ArchiveClient instance to interact with InfoArchive.
    // In this case, we start with a blank installation, and create a sample holding from scratch that will contain
    // the SIP we've just assembled.
    // Use ArchiveClients.usingAlreadyConfiguredServer() instead if you already configured the server with application,
    // holding, etc.
    ArchiveClient archiveClient;
    try (InputStream yaml = getClass().getResourceAsStream("/configuration.yml")) {
      archiveClient = ArchiveClients.configuringServerUsing(
          new YamlBasedConfigurer(new YamlConfiguration(yaml)));
    }

    // Ingest the SIP into InfoArchive
    try (InputStream sip = new FileInputStream(assembledSip)) {
      String aipId = archiveClient.ingestDirect(sip);
      System.out.println("SIP ingested as AIP " + aipId);
    }
  }

  private String relativePath(File file, String rootPath) {
    return file.getAbsolutePath().substring(rootPath.length() + 1);
  }

}
