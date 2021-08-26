/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.opentext.ia.test.TestCase;

public class WhenCreatingPackagingInformation extends TestCase {

  @Test
  @SuppressWarnings("unchecked")
  public void shouldHonorDssIdGenerationStrategy() {
    String id = randomString(8);
    Supplier<String> supplier = mock(Supplier.class);
    when(supplier.get()).thenReturn(id);
    PackagingInformationFactory wrapped = mock(PackagingInformationFactory.class);
    PackagingInformation packagingInformation =
        PackagingInformation.builder().dss().id(randomString(8)).end().build();
    when(wrapped.newInstance(anyLong(), any(Optional.class))).thenReturn(packagingInformation);
    PackagingInformationFactory factory =
        new OneSipPerDssPackagingInformationFactory(wrapped, supplier);

    PackagingInformation actual = factory.newInstance(randomInt(100), Optional.empty());

    assertEquals(id, actual.getDss().getId(), "DSS ID");
  }

}
