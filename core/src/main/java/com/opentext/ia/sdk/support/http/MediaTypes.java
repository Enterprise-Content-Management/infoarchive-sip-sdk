/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;


/**
 * Various media (MIME) types.
 */
public interface MediaTypes {

  String BINARY = "application/octect-stream";
  String HAL = "application/hal+json";
  String XML = "application/xml";
  String TEXT = "text/plain";
  String YAML = "text/yaml"; // NOTE: No IANA registration exists, but most people seem to use this

}
