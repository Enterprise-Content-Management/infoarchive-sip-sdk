/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.client.api.QueryResult;
import com.opentext.ia.sdk.client.impl.QueryResultFactory;
import com.opentext.ia.sdk.support.http.Response;
import com.opentext.ia.test.RandomData;


class WhenCreatingQueryResultFromResponse {

  private QueryResultFactory factory;
  private RandomData data;

  @BeforeEach
  public void before() {
    factory = new QueryResultFactory();
    data = new RandomData();
  }

  @Test
  void shouldReturnNullIfResponseBodyIsNull() throws IOException {
    Response response = mock(Response.class); // NOPMD mock only
    Runnable closer = mock(Runnable.class);
    assertNull(factory.create(response, closer), "Missing query result");
    verify(closer).run();
  }

  @Test
  void shouldCreateResultAndNotCloseResponse() throws IOException {
    int aipQuota = data.integer();
    int resultSetCount = data.integer();
    int aiuQuota = data.integer();
    int resultSetQuota = data.integer();
    Response response = mock(Response.class); // NOPMD mock only
    InputStream body = mock(InputStream.class); // NOPMD mock only

    when(response.getBody()).thenReturn(body);
    when(response.getHeaderValue("cacheOutAipIgnored", false)).thenReturn(Boolean.TRUE);
    when(response.getHeaderValue("aipQuota", 0)).thenReturn(aipQuota);
    when(response.getHeaderValue("resultSetCount", 0)).thenReturn(resultSetCount);
    when(response.getHeaderValue("aiuQuota", 0)).thenReturn(aiuQuota);
    when(response.getHeaderValue("resultSetQuota", 0)).thenReturn(resultSetQuota);

    try (QueryResult result = factory.create(response, () -> { })) {
      assertTrue(result.isCacheOutAipIgnored(), "Is cache-out of AIPs ignored");
      assertEquals(aipQuota, result.getAipQuota(), "AIP quota");
      assertEquals(aiuQuota, result.getAiuQuota(), "AIU quota");
      assertEquals(resultSetCount, result.getResultSetCount(), "# results");
      assertEquals(resultSetQuota, result.getResultSetQuota(), "Result set quota");

      verify(body, never()).close();
      verify(response, never()).close();
    }

    verify(body).close();
    verify(response).close();
  }

}
