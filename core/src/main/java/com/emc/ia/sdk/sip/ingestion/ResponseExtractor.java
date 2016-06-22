/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.ingestion;

import org.apache.http.client.methods.CloseableHttpResponse;


public interface ResponseExtractor<T> {

  T extract(CloseableHttpResponse response);

}
