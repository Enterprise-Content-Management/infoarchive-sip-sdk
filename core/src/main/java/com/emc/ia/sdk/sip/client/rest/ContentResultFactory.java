/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.emc.ia.sdk.sip.client.ContentResult;
import com.emc.ia.sdk.support.http.Response;
import com.emc.ia.sdk.support.http.ResponseFactory;

public class ContentResultFactory implements ResponseFactory<ContentResult> {

  protected ContentResult createResult(String name, int length, String format, InputStream resultStream,
      Closeable dependentResource) {
    return new DefaultContentResult(name, length, format, resultStream, dependentResource);
  }

  @Override
  public ContentResult create(Response response) {
    InputStream resultStream = null;
    boolean resourceOwnershipTransferred = false;
    try {
      final int length = response.getHeaderValue("Content-Length", -1);
      final String format = response.getHeaderValue("Content-Type", "application/octet-stream");
      resultStream = response.getBody();
      // TODO: should we extract name from content disposition?
      final String name = "";
      final ContentResult result = createResult(name, length, format, resultStream, response);
      resourceOwnershipTransferred = true;
      return result;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (!resourceOwnershipTransferred) {
        IOUtils.closeQuietly(resultStream);
        IOUtils.closeQuietly(response);
      }
    }
  }

}
