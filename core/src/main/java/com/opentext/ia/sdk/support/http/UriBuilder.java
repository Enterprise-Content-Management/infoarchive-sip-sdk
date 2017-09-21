/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;


/**
 * Build a URI.
 */
public interface UriBuilder {

  UriBuilder addParameter(String name, String value);

  String build();

}
