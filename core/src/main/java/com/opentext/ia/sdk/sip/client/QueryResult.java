/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client;

import java.io.Closeable;
import java.io.InputStream;


/**
 * Result of executing a query.
 */
public interface QueryResult extends Closeable {

  int getResultSetQuota();

  int getAiuQuota();

  int getResultSetCount();

  int getAipQuota();

  boolean isCacheOutAipIgnored();

  InputStream getResultStream();

}
