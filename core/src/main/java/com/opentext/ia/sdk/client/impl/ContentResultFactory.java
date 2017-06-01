/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.impl;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MIME;
import org.apache.http.message.BasicHeaderValueParser;

import com.opentext.ia.sdk.client.api.ContentResult;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.Response;
import com.opentext.ia.sdk.support.http.ResponseBodyFactory;


/**
 * Factory for creating {@linkplain ContentResult} objects from an HTTP response body (envelope).
 */
public class ContentResultFactory extends ResponseBodyFactory<ContentResult> {

  @Override
  protected ContentResult doCreate(Response response, InputStream resultStream, Runnable closeResult) {
    final int length = response.getHeaderValue("Content-Length", -1);
    final String format = response.getHeaderValue(MIME.CONTENT_TYPE, MediaTypes.BINARY);
    final String name = extractFileNameFrom(response.getHeaderValue(MIME.CONTENT_DISPOSITION, ""));
    return new DefaultContentResult(name, length, format, resultStream, closeResult);
  }

  private String extractFileNameFrom(String contentDisposition) {
    HeaderElement[] elements = BasicHeaderValueParser.parseElements(contentDisposition, null);
    return Arrays.stream(elements)
        .filter(element -> "attachment".equalsIgnoreCase(element.getName()))
        .findAny()
        .map(element -> element.getParameterByName("filename"))
        .map(NameValuePair::getValue)
        .orElse("");
  }

}
