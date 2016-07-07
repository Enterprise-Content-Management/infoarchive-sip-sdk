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
    final StringBuilder builder = new StringBuilder();
    builder.append("IAQueryResult [resultSetQuota=");
    builder.append(resultSetQuota);
    builder.append(", aiuQuota=");
    builder.append(aiuQuota);
    builder.append(", resultSetCount=");
    builder.append(resultSetCount);
    builder.append(", aipQuota=");
    builder.append(aipQuota);
    builder.append(", cacheOutAipIgnored=");
    builder.append(cacheOutAipIgnored);
    builder.append(", resultStream=");
    builder.append(resultStream);
    builder.append(", dependentResource=");
    builder.append(dependentResource);
    builder.append("]");
    return builder.toString();
  }

}
