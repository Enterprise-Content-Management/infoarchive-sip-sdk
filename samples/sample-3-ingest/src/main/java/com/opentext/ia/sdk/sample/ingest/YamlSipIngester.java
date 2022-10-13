/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sample.ingest;

import java.io.File;
import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Map;
//import java.util.Properties;
//
//import org.apache.commons.lang3.StringUtils;

//import com.opentext.ia.sdk.client.api.ArchiveClient;
//import com.opentext.ia.sdk.client.api.ArchiveConnection;
//import com.opentext.ia.sdk.client.factory.ArchiveClients;
import com.opentext.ia.sdk.server.configuration.InfoArchiveConnectionProperties;


@SuppressWarnings("PMD")
public class YamlSipIngester implements InfoArchiveConnectionProperties {

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
    System.out.printf("%nThis test is not working anymore after yaml cleanup refactory, it can be removed - Sample 3: Assemble SIP from %s and ingest into InfoArchive%n", rootPath);
  }

}
