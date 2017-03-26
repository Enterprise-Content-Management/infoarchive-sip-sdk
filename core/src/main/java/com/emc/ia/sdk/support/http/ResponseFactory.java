/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.http;


public interface ResponseFactory<T> {

  T create(Response response, Runnable closeResponse);

}
