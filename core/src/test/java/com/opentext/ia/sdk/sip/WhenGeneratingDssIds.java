/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.datetime.Dates;
import com.opentext.ia.test.TestCase;


class WhenGeneratingDssIds extends TestCase {

  @Test
  void shouldGenerateIdFromPrefixAndSequence() {
    String prefix = randomString(8);
    Supplier<String> dssIdSupplier = new SequentialDssIdSupplier(prefix);

    int n = randomInt(3, 7);
    for (int i = 1; i <= n; i++) {
      assertEquals(prefix + i, dssIdSupplier.get(), "DSS ID #" + i);
    }
  }

  @Test
  void shouldGenerateIdFromPrefixAndDateTime() {
    Date before = new Date();
    String prefix = randomString(8);
    Supplier<String> dssIdSupplier = new DateTimeDssIdSupplier(prefix);

    String actual = dssIdSupplier.get();

    assertTrue(actual.startsWith(prefix), "Not prefixed");
    Date inId = Dates.fromIso(actual.substring(prefix.length()));
    assertTrue(before.compareTo(inId) <= 0, "Too early");
    assertTrue(inId.compareTo(new Date()) <= 0, "Too late");
  }

}
