/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Test;

import com.opentext.ia.sdk.test.TestCase;


public class WhenCreatingPackagingInformation extends TestCase {

  @Test
  @SuppressWarnings("unchecked")
  public void shouldHonorDssIdGenerationStrategy() {
    String id = randomString(8);
    Supplier<String> supplier = mock(Supplier.class);
    when(supplier.get()).thenReturn(id);
    PackagingInformationFactory wrapped = mock(PackagingInformationFactory.class);
    PackagingInformation packagingInformation = PackagingInformation.builder()
      .dss()
      .id(randomString(8))
      .end()
      .build();
    when(wrapped.newInstance(anyInt(), any(Optional.class))).thenReturn(packagingInformation);
    PackagingInformationFactory factory = new OneSipPerDssPackagingInformationFactory(wrapped, supplier);

    PackagingInformation actual = factory.newInstance(randomInt(100), Optional.empty());

    assertEquals("DSS ID", id, actual.getDss()
      .getId());
  }

}
