/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Signals that an exception occurred during an HTTP operation.
 */
@SuppressFBWarnings(value = "IMC_IMMATURE_CLASS_BAD_SERIALVERSIONUID",
    justification = "backward compatiblity")
public class HttpException extends IOException {

  private static final long serialVersionUID = 7518188701006955823L;
  private final int statusCode;

  public HttpException(int statusCode, Throwable cause) {
    super(cause);
    this.statusCode = statusCode;
  }

  public HttpException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

}
