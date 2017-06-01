/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.client.rest;

import java.io.InputStream;

import com.opentext.ia.sdk.sip.client.QueryResult;
import com.opentext.ia.sdk.support.http.Response;
import com.opentext.ia.sdk.support.http.ResponseBodyFactory;


/**
 * Factory for creating {@linkplain QueryResult} objects from an HTTP response body (envelope).
 */

public class QueryResultFactory extends ResponseBodyFactory<QueryResult> {

  @Override
  protected QueryResult doCreate(Response response, InputStream resultStream, Runnable closeResult) {
    boolean cacheOutAipIgnored = response.getHeaderValue("cacheOutAipIgnored", false);
    int aipQuota = response.getHeaderValue("aipQuota", 0);
    int resultSetCount = response.getHeaderValue("resultSetCount", 0);
    int aiuQuota = response.getHeaderValue("aiuQuota", 0);
    int resultSetQuota = response.getHeaderValue("resultSetQuota", 0);
    return new DefaultQueryResult(resultSetQuota, aiuQuota, resultSetCount, aipQuota, cacheOutAipIgnored, resultStream,
        response);
  }

}
