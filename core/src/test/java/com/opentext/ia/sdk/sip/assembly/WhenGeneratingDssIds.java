/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip.assembly;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.function.Supplier;

import org.junit.Test;

import com.opentext.ia.sdk.support.datetime.Dates;
import com.opentext.ia.sdk.support.test.TestCase;


public class WhenGeneratingDssIds extends TestCase {

  @Test
  public void shouldGenerateIdFromPrefixAndSequence() {
    String prefix = randomString(8);
    Supplier<String> dssIdSupplier = new SequentialDssIdSupplier(prefix);

    int n = randomInt(3, 7);
    for (int i = 1; i <= n; i++) {
      assertEquals("DSS ID #" + i, prefix + i, dssIdSupplier.get());
    }
  }

  @Test
  public void shouldGenerateIdFromPrefixAndDateTime() {
    Date before = new Date();
    String prefix = randomString(8);
    Supplier<String> dssIdSupplier = new DateTimeDssIdSupplier(prefix);

    String actual = dssIdSupplier.get();

    assertTrue("Not prefixed", actual.startsWith(prefix));
    Date inId = Dates.fromIso(actual.substring(prefix.length()));
    assertTrue("Too early", before.compareTo(inId) <= 0);
    assertTrue("Too late", inId.compareTo(new Date()) <= 0);
  }

}
