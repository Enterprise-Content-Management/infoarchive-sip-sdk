/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.yaml.configuration;


class ReplaceResultConfigurationHelperYamlWithXml extends ReplaceYamlWithXmlContentVisitor {

  ReplaceResultConfigurationHelperYamlWithXml() {
    super("resultConfigurationHelper", "resultConfigurationHelper", "element", DATA);
  }

}
