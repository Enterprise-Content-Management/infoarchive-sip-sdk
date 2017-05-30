/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.IOUtils;


public abstract class ResponseBodyFactory<T> implements ResponseFactory<T> {

  @Override
  public T create(Response response, Runnable closeResponse) {
    Runnable closeResult = closeResponse;
    AtomicReference<InputStream> resultStream = new AtomicReference<>();
    boolean resourceOwnershipTransferred = false;
    try {
      if (response.getBody() == null) {
        return null;
      }
      resultStream.set(response.getBody());
      closeResult = () -> {
        IOUtils.closeQuietly(resultStream.get());
        closeResponse.run();
      };
      T result = doCreate(response, resultStream.get(), closeResult);
      resourceOwnershipTransferred = true;
      return result;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (!resourceOwnershipTransferred) {
        closeResult.run();
      }
    }
  }

  protected abstract T doCreate(Response response, InputStream resultStream, Runnable closeResult);

}
