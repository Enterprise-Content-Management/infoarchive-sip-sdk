/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client.impl;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicHeaderValueParser;
import org.apache.hc.core5.http.message.ParserCursor;
import org.apache.hc.core5.util.CharArrayBuffer;

import com.opentext.ia.sdk.client.api.ContentResult;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.Response;
import com.opentext.ia.sdk.support.http.ResponseBodyFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Factory for creating {@linkplain ContentResult} objects from an HTTP response body (envelope).
 */
@SuppressFBWarnings(value = "IMPROPER_UNICODE", justification = "not security sensitive")
public class ContentResultFactory extends ResponseBodyFactory<ContentResult> {

  @Override
  protected ContentResult doCreate(Response response, InputStream resultStream, Runnable closeResult) {
    final int length = response.getHeaderValue("Content-Length", -1);
    final String format = response.getHeaderValue("Content-Type", MediaTypes.BINARY);
    final String name = extractFileNameFrom(response.getHeaderValue("Content-Disposition", ""));
    return new DefaultContentResult(name, length, format, resultStream, closeResult);
  }

  private String extractFileNameFrom(String contentDisposition) {
    CharArrayBuffer buffer = new CharArrayBuffer(contentDisposition.length());
    buffer.append(contentDisposition);
    ParserCursor cursor = new ParserCursor(0, buffer.length());
    HeaderElement[] elements = BasicHeaderValueParser.INSTANCE.parseElements(buffer, cursor);

    return Arrays.stream(elements)
            .filter(element -> "attachment".equalsIgnoreCase(element.getName()))
            .findAny()
            .map(element -> element.getParameterByName("filename"))
            .map(NameValuePair::getValue)
            .orElse("");
  }

}
