/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.configuration;


class ReplaceIngestYamlWithXml extends ReplaceYamlWithXmlContentVisitor {

  ReplaceIngestYamlWithXml() {
    super("ingest", "processors", "processor", "processors");
  }

}
