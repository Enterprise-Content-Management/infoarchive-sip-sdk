/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.opentext.ia.sdk.client.api.QueryResult;
import com.opentext.ia.sdk.client.impl.QueryResultFactory;
import com.opentext.ia.sdk.support.http.Response;
import com.opentext.ia.test.RandomData;


public class WhenCreatingQueryResultFromResponse {

  private QueryResultFactory factory;
  private RandomData data;

  @Before
  public void before() {
    factory = new QueryResultFactory();
    data = new RandomData();
  }

  @Test
  public void shouldReturnNullIfResponseBodyIsNull() throws IOException {
    Response response = mock(Response.class);
    Runnable closer = mock(Runnable.class);
    assertNull("Missing query result", factory.create(response, closer));
    verify(closer).run();
  }

  @Test
  public void shouldCreateResultAndNotCloseResponse() throws IOException {
    boolean cacheOutAipIgnored = true;
    int aipQuota = data.integer();
    int resultSetCount = data.integer();
    int aiuQuota = data.integer();
    int resultSetQuota = data.integer();
    Response response = mock(Response.class);
    InputStream body = mock(InputStream.class);

    when(response.getBody()).thenReturn(body);
    when(response.getHeaderValue("cacheOutAipIgnored", false)).thenReturn(cacheOutAipIgnored);
    when(response.getHeaderValue("aipQuota", 0)).thenReturn(aipQuota);
    when(response.getHeaderValue("resultSetCount", 0)).thenReturn(resultSetCount);
    when(response.getHeaderValue("aiuQuota", 0)).thenReturn(aiuQuota);
    when(response.getHeaderValue("resultSetQuota", 0)).thenReturn(resultSetQuota);

    try (QueryResult result = factory.create(response, () -> { })) {
      assertEquals("Is cache-out of AIPs ignored", cacheOutAipIgnored, result.isCacheOutAipIgnored());
      assertEquals("AIP quota", aipQuota, result.getAipQuota());
      assertEquals("AIU quota", aiuQuota, result.getAiuQuota());
      assertEquals("# results", resultSetCount, result.getResultSetCount());
      assertEquals("Result set quota", resultSetQuota, result.getResultSetQuota());

      verify(body, never()).close();
      verify(response, never()).close();
    }

    verify(body).close();
    verify(response).close();
  }

}
