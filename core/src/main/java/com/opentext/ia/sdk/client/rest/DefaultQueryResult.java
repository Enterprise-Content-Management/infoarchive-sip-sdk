/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.rest;

import java.io.Closeable;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.opentext.ia.sdk.client.QueryResult;


/**
 * Default implementation of {@linkplain QueryResult}.
 */
public class DefaultQueryResult implements Closeable, QueryResult {

  private final int resultSetQuota;
  private final int aiuQuota;
  private final int resultSetCount;
  private final int aipQuota;
  private final boolean cacheOutAipIgnored;
  private final InputStream resultStream;
  private final Closeable dependentResource;

  public DefaultQueryResult(int resultSetQuota, int aiuQuota, int resultSetCount, int aipQuota,
      boolean cacheOutAipIgnored, InputStream resultStream, Closeable dependentResource) {
    this.resultSetQuota = resultSetQuota;
    this.aiuQuota = aiuQuota;
    this.resultSetCount = resultSetCount;
    this.aipQuota = aipQuota;
    this.cacheOutAipIgnored = cacheOutAipIgnored;
    this.resultStream = resultStream;
    this.dependentResource = dependentResource;
  }

  @Override
  public int getResultSetQuota() {
    return resultSetQuota;
  }

  @Override
  public int getAiuQuota() {
    return aiuQuota;
  }

  @Override
  public int getResultSetCount() {
    return resultSetCount;
  }

  @Override
  public int getAipQuota() {
    return aipQuota;
  }

  @Override
  public boolean isCacheOutAipIgnored() {
    return cacheOutAipIgnored;
  }

  @Override
  public InputStream getResultStream() {
    return resultStream;
  }

  @Override
  public void close() {
    IOUtils.closeQuietly(resultStream);
    IOUtils.closeQuietly(dependentResource);
  }

  @Override
  public String toString() {
    return new StringBuilder(256).append("DefaultQueryResult [resultSetQuota=")
      .append(resultSetQuota)
      .append(", aiuQuota=")
      .append(aiuQuota)
      .append(", resultSetCount=")
      .append(resultSetCount)
      .append(", aipQuota=")
      .append(aipQuota)
      .append(", cacheOutAipIgnored=")
      .append(cacheOutAipIgnored)
      .append(", resultStream=")
      .append(resultStream)
      .append(", dependentResource=")
      .append(dependentResource)
      .append(']')
      .toString();
  }

}
