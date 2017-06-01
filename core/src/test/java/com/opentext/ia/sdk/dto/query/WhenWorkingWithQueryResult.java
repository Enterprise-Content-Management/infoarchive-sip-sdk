/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.dto.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.opentext.ia.sdk.client.QueryResult;
import com.opentext.ia.sdk.client.rest.DefaultQueryResult;
import com.opentext.ia.sdk.support.test.RandomData;
import com.opentext.ia.sdk.support.test.TestCase;

public class WhenWorkingWithQueryResult extends TestCase {

  private final RandomData random = new RandomData();

  private int resultSetQuota;
  private int aiuQuota;
  private int resultSetCount;
  private int aipQouta;
  private boolean cacheOutIgnored;
  private InputStream stream;
  private Closeable dependentResource;

  @Before
  public void before() {
    resultSetQuota = random.integer();
    aiuQuota = random.integer();
    resultSetCount = random.integer();
    aipQouta = random.integer();
    cacheOutIgnored = false;
    stream = mock(InputStream.class);
    dependentResource = mock(Closeable.class);
  }

  @Test
  public void shouldCloseStreamAndDependentResource() throws IOException {
    DefaultQueryResult result = newQueryResult();
    result.close();
    verify(stream).close();
    verify(dependentResource).close();
  }

  private DefaultQueryResult newQueryResult() {
    return new DefaultQueryResult(resultSetQuota, aiuQuota, resultSetCount, aipQouta, cacheOutIgnored, stream,
        dependentResource);
  }

  @Test
  public void shouldHonorSetValues() throws IOException {
    try (QueryResult result = newQueryResult()) {
      assertEquals(resultSetQuota, result.getResultSetQuota());
      assertEquals(resultSetCount, result.getResultSetCount());
      assertEquals(aipQouta, result.getAipQuota());
      assertEquals(aiuQuota, result.getAiuQuota());
      assertEquals(cacheOutIgnored, result.isCacheOutAipIgnored());
      assertEquals(stream, result.getResultStream());
    }
  }

  @Test
  public void shouldHaveDescriptiveToString() throws IOException {
    try (QueryResult result = newQueryResult()) {
      String string = result.toString();
      assertTrue(string.contains(result.getClass()
        .getSimpleName()));
      assertTrue(string.contains(String.valueOf(aipQouta)));
      assertTrue(string.contains(String.valueOf(aiuQuota)));
      assertTrue(string.contains(String.valueOf(resultSetQuota)));
      assertTrue(string.contains(String.valueOf(resultSetCount)));
      assertTrue(string.contains(String.valueOf(cacheOutIgnored)));
      assertTrue(string.contains(String.valueOf(stream)));
    }
  }

}
