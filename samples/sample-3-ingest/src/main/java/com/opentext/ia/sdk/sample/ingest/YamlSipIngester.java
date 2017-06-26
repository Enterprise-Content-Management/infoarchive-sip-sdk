/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.ingest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.factory.ArchiveClients;
import com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedArchiveConnection;
import com.opentext.ia.sdk.server.configuration.yaml.YamlBasedApplicationConfigurer;
import com.opentext.ia.sdk.sip.*;
import com.opentext.ia.sdk.support.io.FileSupplier;
import com.opentext.ia.sdk.support.io.StringStream;
import com.opentext.ia.sdk.yaml.configuration.YamlConfiguration;
import com.opentext.ia.sdk.yaml.resource.ResourceResolver;


public class YamlSipIngester {

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
    // Load the configuration
    YamlConfiguration configuration = null;
    try (InputStream yaml = getClass().getResourceAsStream("/configuration.yml")) {
      configuration = new YamlConfiguration(yaml, ResourceResolver.fromClasspath());
    }

    // Tell InfoArchive where and how to archive the data
    URI entityUri = URI.create(configuration.getPdiSchemaName());
    String entityName = "animal";
    PackagingInformation prototype = PackagingInformation.builder()
        .dss()
        .application(configuration.getApplicationName())
        .holding(configuration.getHoldingName())
        .producer("SIP-SDK")
        .entity(entityName)
        .schema(entityUri.toString())
        .end()
        .build();

    // Define a mapping from our domain object to the PDI XML
    XmlPdiAssembler<File> pdiAssembler;
    try (InputStream schema = new StringStream(configuration.getPdiSchema())) {
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
    ArchiveClient archiveClient = ArchiveClients.configuringApplicationUsing(new YamlBasedApplicationConfigurer(configuration),
        newArchiveConnection());

    // Ingest the SIP into InfoArchive
    try (InputStream sip = new FileInputStream(assembledSip)) {
      String aipId = archiveClient.ingestDirect(sip);
      System.out.println("SIP ingested as AIP " + aipId);
    }
  }

  private ArchiveConnection newArchiveConnection() throws IOException {
    try (InputStream connectionProperties = getClass().getResourceAsStream("/connection.properties")) {
      return new PropertiesBasedArchiveConnection(connectionProperties);
    }
  }

  private String relativePath(File file, String rootPath) {
    return file.getAbsolutePath().substring(rootPath.length() + 1);
  }

}
