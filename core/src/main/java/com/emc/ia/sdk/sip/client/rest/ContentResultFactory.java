/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.client.rest;

import java.io.InputStream;

import com.emc.ia.sdk.sip.client.ContentResult;
import com.emc.ia.sdk.support.http.Response;
import com.emc.ia.sdk.support.http.ResponseBodyFactory;


public class ContentResultFactory extends ResponseBodyFactory<ContentResult> {

  @Override
  protected ContentResult doCreate(Response response, InputStream resultStream, Runnable closeResult) {
    final int length = response.getHeaderValue("Content-Length", -1);
    final String format = response.getHeaderValue("Content-Type", "application/octet-stream");
    // TODO: should we extract name from content disposition?
    final String name = "";
    return new DefaultContentResult(name, length, format, resultStream, closeResult);
  }

}
