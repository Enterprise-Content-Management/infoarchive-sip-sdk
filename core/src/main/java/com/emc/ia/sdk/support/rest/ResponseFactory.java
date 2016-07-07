/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.support.rest;

import org.apache.http.client.methods.CloseableHttpResponse;


public interface ResponseFactory<T> {

  T create(CloseableHttpResponse response);

}
