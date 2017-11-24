/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.yaml.core;

import org.yaml.snakeyaml.error.MarkedYAMLException;


public class YamlSyntaxErrorException extends MarkedYAMLException {

  public YamlSyntaxErrorException(String message, MarkedYAMLException cause) {
    super(cause.getContext(), cause.getContextMark(), message, cause.getProblemMark());
  }

}
