/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http;


/**
 * Factory for creating objects from an HTTP response.
 * @param <T> The type of objects to create.
 */
@FunctionalInterface
public interface ResponseFactory<T> {

  T create(Response response, Runnable closeResponse);

}
