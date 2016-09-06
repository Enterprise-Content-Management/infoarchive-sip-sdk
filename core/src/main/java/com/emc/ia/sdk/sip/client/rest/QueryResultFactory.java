/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.emc.ia.sdk.support.http.Response;
import com.emc.ia.sdk.support.http.ResponseFactory;
import com.emc.ia.sdk.support.io.RuntimeIoException;

public class QueryResultFactory implements ResponseFactory<DefaultQueryResult> {

  @Override
  public DefaultQueryResult create(Response response) {
    boolean ownershipTransferred = false;
    InputStream responseBody = null;
    try {
      responseBody = response.getBody();
      if (responseBody != null) {
        boolean cacheOutAipIgnored = response.getHeaderValue("cacheOutAipIgnored", false);
        int aipQuota = response.getHeaderValue("aipQuota", 0);
        int resultSetCount = response.getHeaderValue("resultSetCount", 0);
        int aiuQuota = response.getHeaderValue("aiuQuota", 0);
        int resultSetQuota = response.getHeaderValue("resultSetQuota", 0);
        DefaultQueryResult result = new DefaultQueryResult(resultSetQuota, aiuQuota, resultSetCount, aipQuota,
            cacheOutAipIgnored, responseBody, response);
        // We don't close the response, it is instead passed in as a
        // dependent resource to the result which assumes responsibility for closing it.
        ownershipTransferred = true;
        return result;
      }
      return null;
    } catch (IOException e) {
      throw new RuntimeIoException(e);
    } finally {
      if (!ownershipTransferred) {
        IOUtils.closeQuietly(responseBody);
        IOUtils.closeQuietly(response);
      }
    }
  }

}
