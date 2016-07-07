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
      HttpEntity entity = response.getEntity();

      if (entity != null) {
        boolean cacheOutAipIgnored = tryGetBoolean(response, "cacheOutAipIgnored", false);
        int aipQuota = tryGetInt(response, "aipQuota", 0);
        int resultSetCount = tryGetInt(response, "resultSetCount", 0);
        int aiuQuota = tryGetInt(response, "aiuQuota", 0);
        int resultSetQuota = tryGetInt(response, "resultSetQuota", 0);
        resultStream = entity.getContent();
        QueryResult result = new QueryResult(resultSetQuota, aiuQuota, resultSetCount, aipQuota,
            cacheOutAipIgnored, resultStream, response);
        // We don't close the response, it is instead passed in as a
        // dependent resource to the result which assumes responsibility for closing it.
        resourceOwnershipTransferred = true;
        return result;
      }
      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (!resourceOwnershipTransferred) {
        IOUtils.closeQuietly(resultStream);
        IOUtils.closeQuietly(response);
      }
    }
  }

  public int tryGetInt(HttpResponse response, String name, int defaultValue) {
    Header header = response.getFirstHeader(name);
    if (header != null) {
      String value = header.getValue();
      try {
        return Integer.parseInt(value);
      } catch (NumberFormatException exception) {
        // LOG.warn(Fmt.format(
        // "Failed to interpret the value '{}' in the header '{}' as an integer. Will use default value '{}'.", value,
        // name, defaultValue), exception);
      }
    }
    return defaultValue;
  }

  public boolean tryGetBoolean(HttpResponse response, String name, boolean defaultValue) {
    Header header = response.getFirstHeader(name);
    if (header != null) {
      String value = header.getValue();
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
