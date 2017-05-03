/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.sample.ingest;

import static com.emc.ia.sdk.configurer.InfoArchiveConfiguration.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.emc.ia.sdk.configurer.ArchiveClients;
import com.emc.ia.sdk.sip.assembly.*;
import com.emc.ia.sdk.sip.client.ArchiveClient;
import com.emc.ia.sdk.support.io.FileSupplier;


public class SipIngester {

  private static final String SAMPLE_HOLDING = "Animals";
  private static final String SAMPLE_SCHEMA = "pdi-schema.xsd";
  private static final String SAMPLE_NAMESPACE = "urn:emc:ia:schema:sample:animal:1.0";
  private static final String SAMPLE_FILES_PATH = "src/main/resources";

  public static void main(String[] args) {
    try {
      String rootPath = new File(".").getCanonicalPath();
      new SipIngester().run(rootPath);
    } catch (IOException e) {
      e.printStackTrace(); // NOPMD
    }
  }

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
              .element("file_name", relativePath(value, rootPath));
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
    // Use one of the ArchiveClients.client() methods instead if you already have a configured holding.
    ArchiveClient archiveClient = ArchiveClients.withPropertyBasedAutoConfiguration(sampleHoldingConfiguration());

    // Ingest the SIP into InfoArchive
    try (InputStream sip = new FileInputStream(assembledSip)) {
      String aipId = archiveClient.ingestDirect(sip);
      System.out.println("SIP ingested as AIP " + aipId); // NOPMD
    }
  }

  private String relativePath(File file, String rootPath) {
    return file.getAbsolutePath().substring(rootPath.length() + 1);
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> sampleHoldingConfiguration() throws IOException {
    Map<String, String> result = new HashMap<>();
    try (InputStream configuration = getClass().getResourceAsStream("/configuration.properties")) {
      Properties properties = new Properties();
      properties.load(configuration);
      properties.forEach((key, value) -> result.put(key.toString(), value.toString()));
    }
    result.put(APPLICATION_NAME, SAMPLE_HOLDING);
    result.put(HOLDING_NAME, SAMPLE_HOLDING);
    result.put(PDI_SCHEMA_NAME, SAMPLE_NAMESPACE);
    result.put(PDI_SCHEMA, getResource(SAMPLE_SCHEMA));
    result.put(PDI_XML, getResource("pdi.xml"));
    result.put(INGEST_XML, getResource("ingest.xml"));
    return result;
  }

  private String getResource(String name) throws IOException {
    try (InputStream input = getClass().getResourceAsStream("/" + name)) {
      return IOUtils.toString(input);
    }
  }

}
