/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.ingest;

import static com.opentext.ia.sdk.server.configuration.properties.InfoArchiveConfigurationProperties.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.commons.io.IOUtils;

import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.client.factory.ArchiveClients;
import com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedApplicationConfigurer;
import com.opentext.ia.sdk.server.configuration.properties.PropertiesBasedArchiveConnection;
import com.opentext.ia.sdk.sip.*;
import com.opentext.ia.sdk.support.io.FileSupplier;


public class SipIngester {

  private static final String SAMPLE_HOLDING = "Animals";
  private static final String SAMPLE_SCHEMA = "pdi-schema.xsd";
  private static final String SAMPLE_NAMESPACE = "urn:opentext:ia:schema:sample:animal:1.0";
  private static final String SAMPLE_FILES_PATH = "src/main/resources";
  private static final String DATATYPE_STRING = "STRING";

  @SuppressWarnings("PMD.AvoidPrintStackTrace")
  public static void main(String[] args) {
    try {
      String rootPath = new File(".").getCanonicalPath();
      new SipIngester().run(rootPath);
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
    Map<String, String> configuration = sampleHoldingConfiguration();
    configuration.entrySet().stream()
        .map(entry -> entry.getKey() + " = " + entry.getValue())
        .sorted()
        .forEach(System.out::println);
    ArchiveClient archiveClient = ArchiveClients.configuringApplicationUsing(new PropertiesBasedApplicationConfigurer(configuration),
        new PropertiesBasedArchiveConnection(configuration));

    // Ingest the SIP into InfoArchive
    try (InputStream sip = new FileInputStream(assembledSip)) {
      String aipId = archiveClient.ingestDirect(sip);
      System.out.println("SIP ingested as AIP " + aipId);
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

    result.put(AIC_NAME, SAMPLE_HOLDING);

    addCriteria(result, "animalName", "Animal Name", DATATYPE_STRING);
    addCriteria(result, "filePath", "File Path", DATATYPE_STRING);

    String name = "DefaultQuery";
    append(result, QUERY_NAME, name);
    result.put(String.format(QUERY_RESULT_ROOT_ELEMENT_TEMPLATE, name), "result");
    result.put(String.format(QUERY_RESULT_ROOT_NS_ENABLED_TEMPLATE, name), Boolean.TRUE.toString());
    result.put(String.format(QUERY_RESULT_SCHEMA_TEMPLATE, name), SAMPLE_NAMESPACE);
    result.put(String.format(QUERY_NAMESPACE_PREFIX_TEMPLATE, name), "n");
    result.put(String.format(QUERY_NAMESPACE_URI_TEMPLATE, name), SAMPLE_NAMESPACE);
    result.put(String.format(QUERY_XDBPDI_ENTITY_PATH_TEMPLATE, name), "/n:animals/n:animal");
    result.put(String.format(QUERY_XDBPDI_SCHEMA_TEMPLATE, name), SAMPLE_NAMESPACE);
    result.put(String.format(QUERY_XDBPDI_TEMPLATE_TEMPLATE, name), "return $aiu");
    String schema = SAMPLE_NAMESPACE;
    addOperand(result, name, schema, "animalName", "n:animal_name", DATATYPE_STRING, Boolean.TRUE.toString());
    addOperand(result, name, schema, "filePath", "n:file_path", DATATYPE_STRING, Boolean.TRUE.toString());

    result.put(String.format(RESULT_HELPER_SCHEMA_TEMPLATE, "result_helper"), SAMPLE_NAMESPACE);
    result.put(String.format(RESULT_HELPER_XML, "result_helper"), getResource("DefaultResultHelper.xml"));

    new SearchConfigBuilder().name("Find animals")
        .aic(SAMPLE_HOLDING)
        .query("DefaultQuery")
        .formXml(getResource("FindAnimals.form.xml"))
        .result()
        .mainColumn("animalName", "Animal Name", "n:animal_name", DATATYPE_STRING)
        .mainColumn("filePath", "File Path", "n:file_path", DATATYPE_STRING)
        .end()
        .build(result);

    return result;
  }

  private String getResource(String name) throws IOException {
    try (InputStream input = getClass().getResourceAsStream("/" + name)) {
      return IOUtils.toString(input, StandardCharsets.UTF_8);
    }
  }

  private static void addOperand(Map<String, String> result, String query, String schema, String name, String path,
      String type, String indexed) {
    append(result, String.format(QUERY_XDBPDI_OPERAND_NAME, query, schema), name);
    append(result, String.format(QUERY_XDBPDI_OPERAND_PATH, query, schema), path);
    append(result, String.format(QUERY_XDBPDI_OPERAND_TYPE, query, schema), type);
    append(result, String.format(QUERY_XDBPDI_OPERAND_INDEX, query, schema), indexed);
  }

  private static void append(Map<String, String> result, String name, String newValue) {
    String value = result.get(name);
    if (value == null) {
      result.put(name, newValue);
    } else {
      result.put(name, value + "," + newValue);
    }
  }

  private static void addCriteria(Map<String, String> result, String name, String label, String type) {
    addCriteria(result, name, label, type, "", "", "");
  }

  private static void addCriteria(Map<String, String> result, String name, String label, String type,
      String pkeyValuesAttr, String pkeyMinAttr, String pkeyMaxAttr) {
    append(result, CRITERIA_NAME, name);
    append(result, CRITERIA_LABEL, label);
    append(result, CRITERIA_TYPE, type);
    append(result, CRITERIA_PKEYVALUESATTR, pkeyValuesAttr);
    append(result, CRITERIA_PKEYMINATTR, pkeyMinAttr);
    append(result, CRITERIA_PKEYMAXATTR, pkeyMaxAttr);
    append(result, CRITERIA_INDEXED, Boolean.TRUE.toString());
  }

}
