/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.ingest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.opentext.ia.sdk.client.api.ArchiveClient;
import com.opentext.ia.sdk.client.api.ArchiveConnection;
import com.opentext.ia.sdk.client.factory.ArchiveClients;
import com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties;
import com.opentext.ia.sdk.server.configuration.PropertiesBasedArchiveConnection;
import com.opentext.ia.sdk.server.configuration.yaml.YamlBasedApplicationConfigurer;
import com.opentext.ia.sdk.sip.ContentInfo;
import com.opentext.ia.sdk.sip.DigitalObject;
import com.opentext.ia.sdk.sip.DigitalObjectsExtraction;
import com.opentext.ia.sdk.sip.FileGenerationMetrics;
import com.opentext.ia.sdk.sip.FileGenerator;
import com.opentext.ia.sdk.sip.PackagingInformation;
import com.opentext.ia.sdk.sip.SipAssembler;
import com.opentext.ia.sdk.sip.XmlPdiAssembler;
import com.opentext.ia.sdk.support.io.FileSupplier;
import com.opentext.ia.sdk.support.io.StringStream;
import com.opentext.ia.yaml.configuration.YamlConfiguration;

@SuppressWarnings("PMD")
public class YamlSipIngester implements InfoArchiveConnectionProperties {

  private static final String SETTING_USERNAME = "username";
  private static final String SETTING_PASSWORD = "password";
  private static final String SETTING_GATEWAY_URL = "gateway.url";
  private static final String SETTING_CLIENT_ID = "client.id";
  private static final String SETTING_CLIENT_SECRET = "client.secret";

  public static void main(String[] args) {
    try {
      File root = new File("content");
      if (!root.isDirectory()) {
        root = new File("src/main/dist/content");
      }
      String rootPath = root.getCanonicalPath();
      new YamlSipIngester().run(rootPath);
    } catch (IOException e) {
      e.printStackTrace(System.out);
      System.exit(1);
    }
  }

  private void run(String rootPath) throws IOException {
    // System.out.printf("%nSample 3: Assemble SIP from %s and ingest into InfoArchive%n", rootPath);

    // Load the configuration
    YamlConfiguration configuration = new YamlConfiguration(new File(rootPath, "configuration.yml"));

    // Tell InfoArchive where and how to archive the data
    URI entityUri = URI.create(configuration.getPdiSchemaName());
    String entityName = "animal";
    PackagingInformation prototype = PackagingInformation.builder().dss()
        .application(configuration.getApplicationName()).holding(configuration.getHoldingName()).producer("SIP-SDK")
        .entity(entityName).schema(entityUri.toString()).end().build();

    // Define a mapping from our domain object to the PDI XML
    XmlPdiAssembler<File> pdiAssembler;
    try (InputStream schema = new StringStream(configuration.getPdiSchema())) {
      pdiAssembler = new XmlPdiAssembler<File>(entityUri, entityName, schema) {

        @Override
        protected void doAdd(File value, Map<String, ContentInfo> ignored) {
          String name = value.getName();
          getBuilder().element("animal_name", name.substring(0, name.lastIndexOf('.'))).element("file_path",
              relativePath(value, rootPath));
        }
      };
    }

    DigitalObjectsExtraction<File> contentAssembler =
        file -> Collections.singleton(DigitalObject.fromFile(relativePath(file, rootPath), file)).iterator();

    // Assemble the SIP
    SipAssembler<File> sipAssembler = SipAssembler.forPdiAndContent(prototype, pdiAssembler, contentAssembler);
    FileGenerator<File> generator = new FileGenerator<>(sipAssembler, FileSupplier.fromTemporaryDirectory());

    File f1 = new File(rootPath, "ape.dat");
    File f2 = new File(rootPath, "bear.dat");
    File f3 = new File(rootPath, "cobra.dat");

    FileGenerationMetrics metrics = generator.generate(Arrays.asList(f1, f2, f3));
    File assembledSip = metrics.getFile();

    // Get an ArchiveClient instance to interact with InfoArchive.
    // In this case, we start with a blank installation, and create a sample holding from scratch that will contain
    // the SIP we've just assembled.
    // Use ArchiveClients.usingAlreadyConfiguredServer() instead if you already configured the server with application,
    // holding, etc.
    ArchiveClient archiveClient = ArchiveClients
        .configuringApplicationUsing(new YamlBasedApplicationConfigurer(configuration), newArchiveConnection(rootPath));

    // Ingest the SIP into InfoArchive
    try (InputStream sip = Files.newInputStream(assembledSip.toPath(), StandardOpenOption.READ)) {
      String aipId = archiveClient.ingestDirect(sip);
      System.out.println("  SIP ingested as AIP " + aipId);
    }
  }

  private String relativePath(File file, String rootPath) {
    return file.getAbsolutePath().substring(rootPath.length() + 1);
  }

  private ArchiveConnection newArchiveConnection(String rootPath) throws IOException {
    try (InputStream connectionProperties =
        Files.newInputStream(Paths.get(rootPath, "connection.properties"), StandardOpenOption.READ)) {
      Properties properties = new Properties();
      properties.load(connectionProperties);
      override(properties);
      return new PropertiesBasedArchiveConnection(properties);
    }
  }

  private void override(Properties properties) {
    override(SERVER_AUTHENTICATION_GATEWAY, SETTING_GATEWAY_URL, properties);
    override(SERVER_AUTHENTICATION_USER, SETTING_USERNAME, properties);
    override(SERVER_AUTHENTICATION_PASSWORD, SETTING_PASSWORD, properties);
    override(SERVER_CLIENT_ID, SETTING_CLIENT_ID, properties);
    override(SERVER_CLIENT_SECRET, SETTING_CLIENT_SECRET, properties);
  }

  private void override(String key, String overrideProperty, Properties properties) {
    String override = get(overrideProperty);
    if (StringUtils.isNotBlank(override)) {
      properties.setProperty(key, override);
    }
  }

  private static String get(String name) {
    return System.getProperty(name, System.getenv().get(name));
  }

}
