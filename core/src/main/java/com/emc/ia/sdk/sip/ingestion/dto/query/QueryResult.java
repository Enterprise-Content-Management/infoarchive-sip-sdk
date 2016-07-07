/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion.dto.query;

import java.io.Closeable;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class QueryResult implements Closeable {

  private final int resultSetQuota;
  private final int aiuQuota;
  private final int resultSetCount;
  private final int aipQuota;
  private final boolean cacheOutAipIgnored;
  private final InputStream resultStream;
  private final Closeable dependentResource;

  public QueryResult(int resultSetQuota, int aiuQuota, int resultSetCount, int aipQuota, boolean cacheOutAipIgnored,
      InputStream resultStream, Closeable dependentResource) {
    this.resultSetQuota = resultSetQuota;
    this.aiuQuota = aiuQuota;
    this.resultSetCount = resultSetCount;
    this.aipQuota = aipQuota;
    this.cacheOutAipIgnored = cacheOutAipIgnored;
    this.resultStream = resultStream;
    this.dependentResource = dependentResource;
  }

  public int getResultSetQuota() {
    return resultSetQuota;
  }

  public int getAiuQuota() {
    return aiuQuota;
  }

  public int getResultSetCount() {
    return resultSetCount;
  }

  public int getAipQuota() {
    return aipQuota;
  }

  public boolean isCacheOutAipIgnored() {
    return cacheOutAipIgnored;
  }

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
    return new StringBuilder(256)
        .append("IAQueryResult [resultSetQuota=")
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
        .append(dependentResource).append(']')
        .toString();
  }

}
