/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;

public interface UriBuilder {

  UriBuilder addParameter(String name, String value);

  String build();

}
