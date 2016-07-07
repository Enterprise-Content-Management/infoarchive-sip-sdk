/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import com.emc.ia.sdk.sip.ingestion.dto.query.QueryResult;

public class QueryResultFactory implements ResponseFactory<QueryResult> {

  @Override
  public QueryResult create(CloseableHttpResponse response) {
    InputStream resultStream = null;
    boolean resourceOwnershipTransferred = false;

    try {
      final HttpEntity entity = response.getEntity();

      if (entity != null) {
        final boolean cacheOutAipIgnored = tryGetBoolean(response, "cacheOutAipIgnored", false);
        final int aipQuota = tryGetInt(response, "aipQuota", 0);
        final int resultSetCount = tryGetInt(response, "resultSetCount", 0);
        final int aiuQuota = tryGetInt(response, "aiuQuota", 0);
        final int resultSetQuota = tryGetInt(response, "resultSetQuota", 0);
        resultStream = entity.getContent();
        final QueryResult result = new QueryResult(resultSetQuota, aiuQuota, resultSetCount, aipQuota,
            cacheOutAipIgnored, resultStream, response);
        // We don't close the response, it is instead passed in as a
        // dependent resource to the result which assumes responsibility for closing it.
        resourceOwnershipTransferred = true;
        return result;
      }
      return null;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (!resourceOwnershipTransferred) {
        IOUtils.closeQuietly(resultStream);
        IOUtils.closeQuietly(response);
      }
    }
  }

  public int tryGetInt(HttpResponse response, String name, int defaultValue) {
    final Header header = response.getFirstHeader(name);
    if (header != null) {
      final String value = header.getValue();
      try {
        return Integer.parseInt(value);
      } catch (final NumberFormatException exception) {
        // LOG.warn(Fmt.format(
        // "Failed to interpret the value '{}' in the header '{}' as an integer. Will use default value '{}'.", value,
        // name, defaultValue), exception);
      }
    }
    return defaultValue;
  }

  public boolean tryGetBoolean(HttpResponse response, String name, boolean defaultValue) {
    final Header header = response.getFirstHeader(name);
    if (header != null) {
      final String value = header.getValue();
      if (value == null) {
        return defaultValue;
      } else if ("true".equalsIgnoreCase(value)) {
        return true;
      } else if ("false".equalsIgnoreCase(value)) {
        return false;
      }
    }
    return defaultValue;
  }

}
